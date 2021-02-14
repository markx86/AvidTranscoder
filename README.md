# AvidTranscoder
## About this project
I made this ugly-coded program in 6hrs because DaVinci Resolve won't import my mp4 files, so they have to be re-encoded using Avid's DNxHD codec. This program is aimed to automate that task. It can convert multiple files and can automatically find the best profile for FFmpeg.

------------

## Download
Got to the repository's [releases](https://github.com/SkrapeProjects/AvidTranscoder/releases/tag/Release "releases") tab and download the latest version.

------------

## Usage
The program requires Java 8 to run.
You can launch it by running the following command in the folder were the program is stored.
`java -jar AvidTranscoder.jar`
You can launch the program by specifying folders or file to convert, without using the graphical interface.
For example:
`java -jar AvidTranscoder.jar "/path/to/file/to-convert.mp4"`

------------

## Support
Right now the program "only" supports `mp4` and `m4v` files. It "should" support more than that. You can test out new formats by adding the extension in the array `SUPPORTED_FORMATS` located in the file `Formats.java`.
