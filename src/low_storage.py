import os
import subprocess
from dandere2x_core.dandere2x_utils import wait_on_file
from wrappers.frame import Frame

def create_video_from_specific_frames(file_prefix, output, start, end):

    exec = ['ffmpeg',
            '-framerate',
            str(24),
            '-start_number',
            str(start),
            '-i',
            files + "%d.jpg",
            '-vframes',
            str(end),
            '-vf',
            'deband',
            output]

    print(exec)
    subprocess.run(exec)

def delete_specific_merged(file_prefix, start, end):

    # massive headache having to include + 1.
    for x in range(start, end + 1):
        os.remove(file_prefix + str(x) + ".jpg")


def run_low_hdd_usage(fpv: int, frame_count: int, file_template: str, output_template: str):
    for x in range(0, int(frame_count / fpv)):
        wait_on_file(files + str(x * fpv + 1) + ".jpg")
        wait_on_file(files + str(x * fpv + fpv + 1) + ".jpg")
        print("waiting on ")
        print(files + str(x * fpv + fpv + 1) + ".jpg")

        create_video_from_specific_frames(files, output_file + str(x) + ".mkv", x * fpv + 1, fpv)
        delete_specific_merged(files, x * fpv + 1, x * fpv + fpv + 1)

    concate = ''

    for x in range(1, int(frame_count / fpv)):
        concate = concate + output_file + str(x) + ".mkv" + "|"

    concate = concate[0:len(concate) - 1]

    print(concate)


def run_script(workspace: str, fpv: int, frame_count: int):

    merged_files = workspace + "merged\\merged_"
    text_file = open(workspace + "encoded\\list.txt", 'w')

    for x in range(0, int(frame_count / fpv)):

        wait_on_file(files + str(x * fpv + 1) + ".jpg")
        wait_on_file(files + str(x * fpv + fpv) + ".jpg")

        print("waiting on ")
        print(files + str(x * fpv + fpv) + ".jpg")

        encoded_vid = workspace + "encoded\\encoded_" + str(x) + ".mkv"

        create_video_from_specific_frames(merged_files, encoded_vid, x * fpv + 1, fpv)

        text_file.write("file " + "'" + encoded_vid + "'" + "\n")

        wait_on_file(encoded_vid)

        delete_specific_merged(files, x * fpv + 1, x * fpv + fpv)

def merge_encoded_vids(workspace: str, output_file: str):

    text_file = workspace + "encoded\\list.txt"

    exec = ['ffmpeg',
            '-f',
            'concat',
            '-safe',
            str(0),
            '-i',
            text_file,
            '-c:v',
            'libx264',
            output_file]


files = 'C:\\Users\\windwoz\\Desktop\\workspace\\fixed_hopefully\\merged\\merged_'
output_file = 'C:\\Users\\windwoz\\Desktop\\workspace\\fixed_hopefully\\encoded\\merged_'

workspace = "C:\\Users\\windwoz\\Desktop\\workspace\\fixed_hopefully\\"

fpv = 24
frame_count = 240

run_script(workspace, fpv, frame_count)
merge_encoded_vids(workspace, "C:\\Users\\windwoz\\Desktop\\workspace\\fixed_hopefully\\output.mkv")

# exec = ['ffmpeg',
#         '-f',
#         'concat',
#         '-safe',
#         str(0),
#         '-i',
#         'C:\\Users\\windwoz\\Desktop\\workspace\\fixed_hopefully\\encoded\\list.txt',
#         '-c:v',
#         'libx264',
#         'C:\\Users\\windwoz\\Desktop\\workspace\\fixed_hopefully\\encoded\\combined.mkv']
#
# print(exec)
# subprocess.run(exec)



#temp = Frame()
#temp.create_new(3840,2160)

#temp.save_image(files + str(frame_count + 1) + ".jpg")

# for x in range(0, int(frame_count / fpv)):
#
#     wait_on_file(files + str(x * fpv + 1) + ".jpg")
#     wait_on_file(files + str(x * fpv + fpv) + ".jpg")
#     print("waiting on ")
#     print(files + str(x * fpv + fpv) + ".jpg")
#
#     create_video_from_specific_frames(files, output_file + str(x) + ".mkv", x * fpv + 1, fpv)
#
#     wait_on_file(output_file + str(x) + ".mkv")
#     #delete_specific_merged(files, x * fpv + 1, x * fpv + fpv)
#
#
# # upto = int(frame_count / fpv)*fpv + 1
# #
# # wait_on_file(output_file + str(int(frame_count / fpv) +1) + ".mkv")
# #
# # # if os.path.isfile(files + str(upto + 1) + ".jpg") and os.path.isfile(files + str(frame_count) + ".jpg"):
# # create_video_from_specific_frames(files, output_file + str(int(frame_count / fpv) +1) + ".mkv", upto + 1, frame_count - upto + 1)
# # delete_specific_merged(files, upto, frame_count + 1)
#
#
# concate = ''
#
# for x in range(1, int(frame_count / fpv)):
#     concate = concate + output_file + str(x) + ".mkv" + "|"
#
# concate = concate[0:len(concate)-1]
#
# commands = ['ffmpeg',
#             '']
#
# print(concate)
#


#create_video_from_specific_frames(files, output, 100, 220)

#delete_specific_merged(files, 1, 20)