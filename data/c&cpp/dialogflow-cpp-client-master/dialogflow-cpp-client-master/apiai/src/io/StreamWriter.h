/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#ifndef STREAM_WRITER_H
#define STREAM_WRITER_H

#include <sstream>

namespace ai {
    namespace io {

        class Stream;

        class StreamWriter {
        public:

            StreamWriter(Stream &stream);

            StreamWriter(const StreamWriter &writer);

            StreamWriter &write(const std::string &source);

            StreamWriter &write(const char *source, std::streamsize count);

            bool sealed();

            void sealed(bool sealed);

            std::string str();

            void str(const std::string &string);

            void flush();

        private:

            Stream &stream_;
            std::stringstream stringstream_;
        };
    }
}

#endif // STREAM_WRITER_H
