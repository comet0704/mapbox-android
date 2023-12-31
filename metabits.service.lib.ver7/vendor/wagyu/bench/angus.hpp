/*******************************************************************************
 *                                                                              *
 * Author    :  Angus Johnson                                                   *
 * Version   :  6.4.0                                                           *
 * Date      :  2 July 2015                                                     *
 * Website   :  http://www.angusj.com                                           *
 * Copyright :  Angus Johnson 2010-2015                                         *
 *                                                                              *
 * License:                                                                     *
 * Use, modification & distribution is subject to Boost Software License Ver 1. *
 * http://www.boost.org/LICENSE_1_0.txt                                         *
 *                                                                              *
 * Attributions:                                                                *
 * The code in this library is an extension of Bala Vatti's clipping algorithm: *
 * "A generic solution to polygon clipping"                                     *
 * Communications of the ACM, Vol 35, Issue 7 (July 1992) pp 56-63.             *
 * http://portal.acm.org/citation.cfm?id=129906                                 *
 *                                                                              *
 * Computer graphics and geometric modeling: implementation and algorithms      *
 * By Max K. Agoston                                                            *
 * Springer; 1 edition (January 4, 2005)                                        *
 * http://books.google.com/books?q=vatti+clipping+agoston                       *
 *                                                                              *
 * See also:                                                                    *
 * "Polygon Offsetting by Computing Winding Numbers"                            *
 * Paper no. DETC2005-85513 pp. 565-575                                         *
 * ASME 2005 International Design Engineering Technical Conferences             *
 * and Computers and Information in Engineering Conference (IDETC/CIE2005)      *
 * September 24-28, 2005 , Long Beach, California, USA                          *
 * http://www.me.berkeley.edu/~mcmains/pubs/DAC05OffsetPolygon.pdf              *
 *                                                                              *
 *******************************************************************************/

#ifndef clipper_hpp
#define clipper_hpp
#pragma GCC diagnostic push
#pragma GCC diagnostic ignored "-Wpragmas"
#pragma GCC diagnostic ignored "-Wold-style-cast"
#pragma GCC diagnostic ignored "-Weffc++"

#define CLIPPER_INTPOINT_IMPL mapbox::geometry::point<cInt>
#define CLIPPER_PATH_IMPL mapbox::geometry::linear_ring<cInt>
#define CLIPPER_PATHS_IMPL mapbox::geometry::polygon<cInt>
#define CLIPPER_IMPL_INCLUDE "mapbox/geometry/polygon.hpp"

#define CLIPPER_VERSION "6.2.6"

// use_int32: When enabled 32bit ints are used instead of 64bit ints. This
// improve performance but coordinate values are limited to the range +/- 46340
//#define use_int32

// use_xyz: adds a Z member to IntPoint. Adds a minor cost to perfomance.
//#define use_xyz

// use_lines: Enables line clipping. Adds a very minor cost to performance.
//#define use_lines

// use_deprecated: Enables temporary support for the obsolete functions
//#define use_deprecated

#include <cstdlib>
#include <cstring>
#include <functional>
#include <list>
#include <ostream>
#include <queue>
#include <set>
#include <stdexcept>
#include <unordered_map>
#include <vector>
#if defined(CLIPPER_IMPL_INCLUDE)
#include CLIPPER_IMPL_INCLUDE
#endif

namespace ClipperLib {

enum ClipType { ctIntersection, ctUnion, ctDifference, ctXor };
enum PolyType { ptSubject, ptClip };
// By far the most widely used winding rules for polygon filling are
// EvenOdd & NonZero (GDI, GDI+, XLib, OpenGL, Cairo, AGG, Quartz, SVG, Gr32)
// Others rules include Positive, Negative and ABS_GTR_EQ_TWO (only in OpenGL)
// see http://glprogramming.com/red/chapter11.html
enum PolyFillType { pftEvenOdd, pftNonZero, pftPositive, pftNegative };

#ifdef use_int32
typedef int cInt;
static cInt const loRange = 0x7FFF;
static cInt const hiRange = 0x7FFF;
#else
typedef std::int64_t cInt;
static cInt const loRange = 0x3FFFFFFF;
static cInt const hiRange = 0x3FFFFFFFFFFFFFFFLL;
typedef signed long long long64; // used by Int128 class
typedef unsigned long long ulong64;

#endif

#if defined(CLIPPER_INTPOINT_IMPL)

typedef CLIPPER_INTPOINT_IMPL IntPoint;

#else

struct IntPoint {
    cInt x;
    cInt y;
#ifdef use_xyz
    cInt Z;
    IntPoint(cInt _x = 0, cInt _y = 0, cInt z = 0) : x(_x), y(_y), Z(z){};
#else
    IntPoint(cInt _x = 0, cInt _y = 0) : x(_x), y(_y){};
#endif

    friend inline bool operator==(const IntPoint& a, const IntPoint& b) {
        return a.x == b.x && a.y == b.y;
    }
    friend inline bool operator!=(const IntPoint& a, const IntPoint& b) {
        return a.x != b.x || a.y != b.y;
    }
};
#endif

//------------------------------------------------------------------------------

#if defined(CLIPPER_PATH_IMPL)

typedef CLIPPER_PATH_IMPL Path;

#else

typedef std::vector<IntPoint> Path;

#endif

#if defined(CLIPPER_PATHS_IMPL)

typedef CLIPPER_PATHS_IMPL Paths;

#else

typedef std::vector<Path> Paths;

#endif

inline Path& operator<<(Path& poly, const IntPoint& p) {
    poly.push_back(p);
    return poly;
}
inline Paths& operator<<(Paths& polys, const Path& p) {
    polys.push_back(p);
    return polys;
}

std::ostream& operator<<(std::ostream& s, const IntPoint& p);
std::ostream& operator<<(std::ostream& s, const Path& p);
std::ostream& operator<<(std::ostream& s, const Paths& p);

struct DoublePoint {
    double x;
    double y;
    DoublePoint(double _x = 0, double _y = 0) : x(_x), y(_y) {
    }
    DoublePoint(IntPoint ip) : x((double)ip.x), y((double)ip.y) {
    }
};
//------------------------------------------------------------------------------

#ifdef use_xyz
typedef void (*ZFillCallback)(IntPoint& e1bot, IntPoint& e1top, IntPoint& e2bot, IntPoint& e2top, IntPoint& pt);
#endif

enum InitOptions { ioReverseSolution = 1, ioStrictlySimple = 2, ioPreserveCollinear = 4 };
enum JoinType { jtSquare, jtRound, jtMiter };
enum EndType { etClosedPolygon, etClosedLine, etOpenButt, etOpenSquare, etOpenRound };

class PolyNode;
typedef std::vector<PolyNode*> PolyNodes;

class PolyNode {
public:
    PolyNode();
    virtual ~PolyNode(){};
    Path Contour;
    PolyNodes Childs;
    PolyNode* Parent;
    PolyNode* GetNext() const;
    bool IsHole() const;
    bool IsOpen() const;
    int ChildCount() const;

private:
    unsigned Index; // node index in Parent.Childs
    bool m_IsOpen;
    JoinType m_jointype;
    EndType m_endtype;
    PolyNode* GetNextSiblingUp() const;
    void AddChild(PolyNode& child);
    friend class Clipper; // to access Index
    friend class ClipperOffset;
};

class PolyTree : public PolyNode {
public:
    ~PolyTree() {
        Clear();
    };
    PolyNode* GetFirst() const;
    void Clear();
    int Total() const;

private:
    PolyNodes AllNodes;
    friend class Clipper; // to access AllNodes
};

bool Orientation(const Path& poly);
double Area(const Path& poly);
int PointInPolygon(const IntPoint& pt, const Path& path);

void SimplifyPolygon(const Path& in_poly, Paths& out_polys, PolyFillType fillType = pftEvenOdd);
void SimplifyPolygons(const Paths& in_polys, Paths& out_polys, PolyFillType fillType = pftEvenOdd);
void SimplifyPolygons(Paths& polys, PolyFillType fillType = pftEvenOdd);

void CleanPolygon(const Path& in_poly, Path& out_poly, double distance = 1.415);
void CleanPolygon(Path& poly, double distance = 1.415);
void CleanPolygons(const Paths& in_polys, Paths& out_polys, double distance = 1.415);
void CleanPolygons(Paths& polys, double distance = 1.415);

void MinkowskiSum(const Path& pattern, const Path& path, Paths& solution, bool pathIsClosed);
void MinkowskiSum(const Path& pattern, const Paths& paths, Paths& solution, bool pathIsClosed);
void MinkowskiDiff(const Path& poly1, const Path& poly2, Paths& solution);

void PolyTreeToPaths(const PolyTree& polytree, Paths& paths);
void ClosedPathsFromPolyTree(const PolyTree& polytree, Paths& paths);
void OpenPathsFromPolyTree(PolyTree& polytree, Paths& paths);

void ReversePath(Path& p);
void ReversePaths(Paths& p);

struct IntRect {
    cInt left;
    cInt top;
    cInt right;
    cInt bottom;
};

// enums that are used internally ...
enum EdgeSide { esLeft = 1, esRight = 2 };

// forward declarations (for stuff used internally) ...
struct TEdge;
struct IntersectNode;
struct LocalMinimum;
struct OutPt;
struct OutRec;
struct Join;
struct OutPtIntersect;

typedef std::vector<OutRec*> PolyOutList;
typedef std::vector<TEdge*> EdgeList;
typedef std::vector<Join*> JoinList;
typedef std::vector<IntersectNode*> IntersectList;

//------------------------------------------------------------------------------

// ClipperBase is the ancestor to the Clipper class. It should not be
// instantiated directly. This class simply abstracts the conversion of sets of
// polygon coordinates into edge objects that are stored in a LocalMinima list.
class ClipperBase {
public:
    ClipperBase();
    virtual ~ClipperBase();
    virtual bool AddPath(const Path& pg, PolyType PolyTyp, bool Closed);
    bool AddPaths(const Paths& ppg, PolyType PolyTyp, bool Closed);
    virtual void Clear();
    IntRect GetBounds();
    bool PreserveCollinear() {
        return m_PreserveCollinear;
    };
    void PreserveCollinear(bool value) {
        m_PreserveCollinear = value;
    };

protected:
    void DisposeLocalMinimaList();
    TEdge* AddBoundsToLML(TEdge* e, bool IsClosed);
    virtual void Reset();
    TEdge* ProcessBound(TEdge* E, bool NextIsForward);
    void InsertScanbeam(const cInt Y);
    bool PopScanbeam(cInt& Y);
    bool LocalMinimaPending();
    bool PopLocalMinima(cInt Y, const LocalMinimum*& locMin);
    OutRec* CreateOutRec();
    void DisposeAllOutRecs();
    void DisposeOutRec(PolyOutList::size_type index);
    void SwapPositionsInAEL(TEdge* Edge1, TEdge* Edge2);
    void DeleteFromAEL(TEdge* e);
    void UpdateEdgeIntoAEL(TEdge*& e);

    typedef std::vector<LocalMinimum> MinimaList;
    MinimaList::iterator m_CurrentLM;
    MinimaList m_MinimaList;

    bool m_UseFullRange;
    EdgeList m_edges;
    bool m_PreserveCollinear{};
    bool m_HasOpenPaths{};
    PolyOutList m_PolyOuts;
    TEdge* m_ActiveEdges{};

    typedef std::priority_queue<cInt> ScanbeamList;
    ScanbeamList m_Scanbeam;
};
//------------------------------------------------------------------------------

class Clipper : public virtual ClipperBase {
public:
    Clipper(int initOptions = 0);
    bool Execute(ClipType clipType, Paths& solution, PolyFillType fillType = pftEvenOdd);
    bool Execute(ClipType clipType, Paths& solution, PolyFillType subjFillType, PolyFillType clipFillType);
    bool Execute(ClipType clipType, PolyTree& polytree, PolyFillType fillType = pftEvenOdd);
    bool Execute(ClipType clipType, PolyTree& polytree, PolyFillType subjFillType, PolyFillType clipFillType);
    bool ReverseSolution() {
        return m_ReverseOutput;
    };
    void ReverseSolution(bool value) {
        m_ReverseOutput = value;
    };
    bool StrictlySimple() {
        return m_StrictSimple;
    };
    void StrictlySimple(bool value) {
        m_StrictSimple = value;
    };
        // set the callback function for z value filling on intersections (otherwise Z is 0)
#ifdef use_xyz
    void ZFillFunction(ZFillCallback zFillFunc);
#endif
protected:
    virtual bool ExecuteInternal();

private:
    JoinList m_Joins;
    JoinList m_GhostJoins;
    IntersectList m_IntersectList;
    ClipType m_ClipType;
    typedef std::list<cInt> MaximaList;
    MaximaList m_Maxima;
    TEdge* m_SortedEdges{};
    bool m_ExecuteLocked;
    PolyFillType m_ClipFillType;
    PolyFillType m_SubjFillType;
    bool m_ReverseOutput;
    bool m_UsingPolyTree{};
    bool m_StrictSimple;
#ifdef use_xyz
    ZFillCallback m_ZFill; // custom callback
#endif
    void SetWindingCount(TEdge& edge);
    bool IsEvenOddFillType(const TEdge& edge) const;
    bool IsEvenOddAltFillType(const TEdge& edge) const;
    void InsertLocalMinimaIntoAEL(const cInt botY);
    void InsertEdgeIntoAEL(TEdge* edge, TEdge* startEdge);
    void AddEdgeToSEL(TEdge* edge);
    bool PopEdgeFromSEL(TEdge*& edge);
    void CopyAELToSEL();
    void DeleteFromSEL(TEdge* e);
    void SwapPositionsInSEL(TEdge* Edge1, TEdge* Edge2);
    bool IsContributing(const TEdge& edge) const;
    bool IsTopHorz(const cInt XPos);
    void DoMaxima(TEdge* e);
    void ProcessHorizontals();
    void ProcessHorizontal(TEdge* horzEdge);
    void AddLocalMaxPoly(TEdge* e1, TEdge* e2, const IntPoint& Pt);
    OutPt* AddLocalMinPoly(TEdge* e1, TEdge* e2, const IntPoint& Pt);
    OutRec* GetOutRec(int Idx);
    void AppendPolygon(TEdge* e1, TEdge* e2);
    void IntersectEdges(TEdge* e1, TEdge* e2, IntPoint& Pt);
    OutPt* AddOutPt(TEdge* e, const IntPoint& pt);
    OutPt* GetLastOutPt(TEdge* e);
    bool ProcessIntersections(const cInt topY);
    void BuildIntersectList(const cInt topY);
    void ProcessIntersectList();
    void ProcessEdgesAtTopOfScanbeam(const cInt topY);
    void BuildResult(Paths& polys);
    void BuildResult2(PolyTree& polytree);
    void SetHoleState(TEdge* e, OutRec* outrec);
    void DisposeIntersectNodes();
    bool FixupIntersectionOrder();
    void FixupOutPolygon(OutRec& outrec);
    void FixupOutPolyline(OutRec& outrec);
    bool IsHole(TEdge* e);
    bool FindOwnerFromSplitRecs(OutRec& outRec, OutRec*& currOrfl);
    void FixHoleLinkage(OutRec& outrec);
    void AddJoin(OutPt* op1, OutPt* op2, const IntPoint OffPt);
    void ClearJoins();
    void ClearGhostJoins();
    void AddGhostJoin(OutPt* op, const IntPoint OffPt);
    bool JoinPoints(Join* j, OutRec* outRec1, OutRec* outRec2);
    void JoinCommonEdges();
    void DoSimplePolygons();
    bool FindIntersectLoop(std::unordered_multimap<int, OutPtIntersect>& dupeRec,
                           std::list<std::pair<int, OutPtIntersect>>& iList,
                           OutRec* outRec_parent,
                           int idx_origin,
                           int idx_search,
                           std::set<int>& visited,
                           OutPt* orig_pt,
                           OutPt* prev_pt);
    bool FixIntersects(std::unordered_multimap<int, OutPtIntersect>& dupeRec,
                       OutPt* op_j,
                       OutPt* op_k,
                       OutRec* outRec_j,
                       OutRec* outRec_k);
    void FixupFirstLefts1(OutRec* OldOutRec, OutRec* NewOutRec);
    void FixupFirstLefts2(OutRec* InnerOutRec, OutRec* OuterOutRec);
    void FixupFirstLefts3(OutRec* OldOutRec, OutRec* NewOutRec);
#ifdef use_xyz
    void SetZ(IntPoint& pt, TEdge& e1, TEdge& e2);
#endif
};
//------------------------------------------------------------------------------

class ClipperOffset {
public:
    ClipperOffset(double miterLimit = 2.0, double arcTolerance = 0.25);
    ~ClipperOffset();
    void AddPath(const Path& path, JoinType joinType, EndType endType);
    void AddPaths(const Paths& paths, JoinType joinType, EndType endType);
    void Execute(Paths& solution, double delta);
    void Execute(PolyTree& solution, double delta);
    void Clear();
    double MiterLimit;
    double ArcTolerance;

private:
    Paths m_destPolys;
    Path m_srcPoly;
    Path m_destPoly;
    std::vector<DoublePoint> m_normals;
    double m_delta{}, m_sinA{}, m_sin{}, m_cos{};
    double m_miterLim{}, m_StepsPerRad{};
    IntPoint m_lowest;
    PolyNode m_polyNodes;

    void FixOrientations();
    void DoOffset(double delta);
    void OffsetPoint(int j, int& k, JoinType jointype);
    void DoSquare(int j, int k);
    void DoMiter(int j, int k, double r);
    void DoRound(int j, int k);
};
//------------------------------------------------------------------------------

class clipperException : public std::exception {
public:
    clipperException(const char* description) : m_descr(description) {
    }
    virtual ~clipperException() throw() {
    }
    virtual const char* what() const throw() {
        return m_descr.c_str();
    }

private:
    std::string m_descr;
};
//------------------------------------------------------------------------------

} // namespace ClipperLib

#pragma GCC diagnostic pop
#endif // clipper_hpp
