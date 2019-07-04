from context import Context
from wrappers.frame import Frame


#todo this file is named incorrectly
# - need a resume feature

def compress_frames(context: Context):
    inputs_dir = context.input_frames_dir
    frame_count = context.frame_count
    compressed_dir = context.compressed_dir
    quality_low = context.quality_low
    extension_type = context.extension_type

    for x in range(1, frame_count + 1):
        frame = Frame()
<<<<<<< Updated upstream:src/dandere2x_core/mse_computer.py

        frame.load_from_string(inputs_dir + "frame" + str(x) + ".jpg")
        frame.save_image_quality(compressed_dir + str(x) + ".jpg", quality_low)
=======
        frame.load_from_string(inputs_dir + "frame" + str(x) + extension_type)
        frame.save_image_quality(compressed_dir + "compressed_" + str(x) + ".jpg", quality_low)
>>>>>>> Stashed changes:src/dandere2x_core/frame_compressor.py
