set(MBGL_FILESOURCE_FILES

        ##################################### platform/default/filesource.cmake - filesource-files.json ############################
        # sources
        ${LIB_MBGL_PLATFORM}/default/asset_file_source.cpp
        ${LIB_MBGL_PLATFORM}/default/default_file_source.cpp
        ${LIB_MBGL_PLATFORM}/default/file_source_request.cpp
        ${LIB_MBGL_PLATFORM}/default/local_file_request.cpp
        ${LIB_MBGL_PLATFORM}/default/local_file_source.cpp
        ${LIB_MBGL_PLATFORM}/default/mbgl/storage/offline.cpp
        ${LIB_MBGL_PLATFORM}/default/mbgl/storage/offline_database.cpp
        ${LIB_MBGL_PLATFORM}/default/mbgl/storage/offline_download.cpp
        ${LIB_MBGL_PLATFORM}/default/online_file_source.cpp

        # public_headers
        ${LIB_MBGL_INCLUDE}/mbgl/storage/default_file_source.hpp
        ${LIB_MBGL_INCLUDE}/mbgl/storage/offline.hpp
        ${LIB_MBGL_INCLUDE}/mbgl/storage/online_file_source.hpp
        ${LIB_MBGL_PLATFORM}/default/mbgl/storage/file_source_request.hpp
        ${LIB_MBGL_PLATFORM}/default/mbgl/storage/local_file_request.hpp
        ${LIB_MBGL_PLATFORM}/default/mbgl/storage/merge_sideloaded.hpp
        ${LIB_MBGL_PLATFORM}/default/mbgl/storage/offline_database.hpp
        ${LIB_MBGL_PLATFORM}/default/mbgl/storage/offline_download.hpp
        ${LIB_MBGL_PLATFORM}/default/mbgl/storage/offline_schema.hpp
        ${LIB_MBGL_PLATFORM}/default/mbgl/storage/sqlite3.hpp

        # private_headers
        ${LIB_MBGL_SRC}/mbgl/storage/asset_file_source.hpp
        ${LIB_MBGL_SRC}/mbgl/storage/http_file_source.hpp
        ${LIB_MBGL_SRC}/mbgl/storage/local_file_source.hpp


        )