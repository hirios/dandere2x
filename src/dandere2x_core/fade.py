import logging
from dataclasses import dataclass

from wrappers.frame import Frame
import numpy as np


# A simple struct to hold the data to produce a fade.
@dataclass
class FadeData:
    x: int
    y: int
    scalar: int

# comments: I'm not sure if we need to create a new frame, it's not causing
# preformance issues so far, but it's easier for me to debug knowing that it creates
# a copy than reassigns. Perhaps someone can test if we can just edit the input rather than
# change the fade.

def fade_image(context, block_size, frame_base: Frame, list_correction: list):
    logger = logging.getLogger(__name__)

    # load context
    scale_factor = int(context.scale_factor)

    fade_list = []
    out_image = Frame()
    out_image.create_new(frame_base.width, frame_base.height)
    out_image.copy_image(frame_base)

    fade_data_size = 3

    for x in range(int(len(list_correction) / fade_data_size)):
        fade_list.append(FadeData(int(list_correction[x * fade_data_size + 0]),
                                  int(list_correction[x * fade_data_size + 1]),
                                  int(list_correction[x * fade_data_size + 2])))

    # copy over predictive vectors into new image
    for vector in fade_list:
        out_image.fade_block(vector.x * scale_factor,
                             vector.y * scale_factor,
                             block_size * scale_factor,
                             vector.scalar)

        out_image.frame = np.clip(out_image.frame, 0, 255)

    copy = Frame()

    copy.create_new(out_image.width, out_image.height)

    copy.copy_image(out_image)


    print(np.amax(out_image.frame))
    # # dev notes - why 252? Artifacts still appear when it's at 255. Perhaps Encoding problem?
    #out_image.frame = np.clip(out_image.frame, 0, 255)

    print("difference: ")
    print(out_image.mean(copy))

    return out_image
