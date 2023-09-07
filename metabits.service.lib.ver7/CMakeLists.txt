cmake_minimum_required(VERSION 3.9)
project(mbgl LANGUAGES CXX C)


# Mapbox 수정 - 변수 설정 추가
set(LOCAL_PATH ${CMAKE_SOURCE_DIR}/src/main/jni)
set(LIB_MBGL_INCLUDE ${LOCAL_PATH}/include)
set(LIB_MBGL_PLATFORM ${LOCAL_PATH}/platform)
set(LIB_MBGL_SRC ${LOCAL_PATH}/core)
set(IMPORT_PKG_DIR ${CMAKE_SOURCE_DIR}/vendor)



# Mapbox 수정 - mbgl core 파일 리스트 추가
include(mbgl_core_files.cmake)

#set(CMAKE_CONFIGURATION_TYPES Debug Release)
set(CMAKE_CONFIGURATION_TYPES Release)

# 컴파일 옵션
# 에러 메시지 "non-type template argument evaluates to 14695981039346656037, which cannot be narrowed to type 'unsigned int' [-Wc++11-narrowing]" 발생으로 -Wno-narrowing 추가
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fPIC -D NDEBUG -D __ANDROID__ -D MBGL_USE_GLES2 -D RAPIDJSON_HAS_STDSTRING -std=c++14 -Wno-narrowing -ftemplate-depth=1024 -frtti -fexceptions -g -O3 -Werror -Wall -Wextra -Wshadow -Wno-variadic-macros -Wno-error=unused-parameter -Wno-unknown-pragmas")

# MGBL 라이브러리 버전 설정 - mbgl/util/version.hpp와 연동
set(MBGL_VERSION_REV 00000000)
set_source_files_properties(${LIB_MBGL_SRC}/mbgl/util/version.cpp PROPERTIES COMPILE_DEFINITIONS MBGL_VERSION_REV="${MBGL_VERSION_REV}")



# 테스트 로그 출력용
#message(SEND_ERROR "MBGL_CORE_FILES = ${MBGL_CORE_FILES}")



# mbgl-core 라이브러리(libmbgl-core.a) -  metabits.service.lib.ver7\.externalNativeBuild\cmake\debug\armeabi-v7a 폴더에 만들어진다
add_library(mbgl-core STATIC
        ${MBGL_CORE_FILES}
        )

target_compile_options(mbgl-core
        PRIVATE -fPIC
        PRIVATE -fvisibility-inlines-hidden
        )

target_include_directories( mbgl-core
        PUBLIC
        ${LIB_MBGL_INCLUDE}
        ${LIB_MBGL_SRC}
        ${LIB_MBGL_PLATFORM}/default
        ${LIB_MBGL_PLATFORM}/linux
        ${IMPORT_PKG_DIR}/benchmark/include
        ${IMPORT_PKG_DIR}/boost/include
        ${IMPORT_PKG_DIR}/cheap-ruler-cpp/include
        ${IMPORT_PKG_DIR}/earcut.hpp/include
        ${IMPORT_PKG_DIR}/eternal/include
        ${IMPORT_PKG_DIR}/expected/include
        ${IMPORT_PKG_DIR}/geojson.hpp/include
        ${IMPORT_PKG_DIR}/geojson-vt-cpp/include
        ${IMPORT_PKG_DIR}/geometry.hpp/include
        ${IMPORT_PKG_DIR}/glfw/include
        ${IMPORT_PKG_DIR}/gtest/include
        ${IMPORT_PKG_DIR}/icu/include
        ${IMPORT_PKG_DIR}/jni.hpp/include
        ${IMPORT_PKG_DIR}/kdbush.hpp/include
        ${IMPORT_PKG_DIR}/nunicode/include
        ${IMPORT_PKG_DIR}/Optional
        ${IMPORT_PKG_DIR}/pixelmatch-cpp/include
        ${IMPORT_PKG_DIR}/polylabel/include
        ${IMPORT_PKG_DIR}/protozero/include
        ${IMPORT_PKG_DIR}/polylabel/include
        ${IMPORT_PKG_DIR}/rapidjson/include
        ${IMPORT_PKG_DIR}/shelf-pack-cpp/include
        ${IMPORT_PKG_DIR}/sqlite/include
        ${IMPORT_PKG_DIR}/supercluster.hpp/include
        ${IMPORT_PKG_DIR}/unique_resource
        ${IMPORT_PKG_DIR}/variant/include
        ${IMPORT_PKG_DIR}/vector-tile/include
        ${IMPORT_PKG_DIR}/wagyu/include

        )

target_compile_options(mbgl-core
        PRIVATE -ffunction-sections
        PRIVATE -fdata-sections
        PRIVATE -Os
        )

target_link_libraries(mbgl-core
        #PRIVATE lib_nu
        #PRIVATE lib_sqlite3
        #PRIVATE lib_gtest
        #PRIVATE lib_icuuc
        PUBLIC -llog
        PUBLIC -landroid
        PUBLIC -ljnigraphics
        PUBLIC -lEGL
        PUBLIC -lGLESv2
        PUBLIC -lstdc++
        PUBLIC -latomic
        PUBLIC -lz
        PUBLIC -Wl,--gc-sections
        )



# geomaps-gl 라이브러리(libgeomaps-gl.so)
add_library( # name of the library.
        geomaps-gl

        # library as a shared library.
        SHARED

        # relative path to source file(s)
        # Associated headers in the same location as their source file are automatically included.
        ${LIB_MBGL_PLATFORM}/android/src/main.cpp
        )


target_link_libraries( # target library.
        geomaps-gl
        PUBLIC mbgl-core
)


set_property(GLOBAL PROPERTY USE_FOLDERS ON)



#아래 부분이 없으면 declaration shadows a typedef in 'segment_intersection_info<CoordinateType, SegmentRatio>' [-Werror,-Wshadow] 에러 발생
# Set the maximum number of template instantiation notes for a single warning or error to n. The default value is 10.
#if (CMAKE_HOST_SYSTEM_NAME STREQUAL "Windows")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -ftemplate-backtrace-limit=0 -w")
    set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -w")
#else()
#    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -ftemplate-depth=1024 -Wall -Wextra -Wshadow -Wnon-virtual-dtor -Wno-variadic-macros -Wno-unknown-pragmas")
#    set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wall -Wextra -Wshadow -Wno-variadic-macros -Wno-unknown-pragmas")
#    message(SEND_ERROR "CMAKE_HOST_SYSTEM_NAME = ${CMAKE_HOST_SYSTEM_NAME}")
#endif()



