from wrappers.frame import Frame
import numpy as np


f1 = Frame()

f1.load_from_string("C:\\Users\\windwoz\\Desktop\\workspace\\broken\\merged\\merged_3.png")

f1.fade_block(0,0,300,1000)

f1.frame = np.clip(f1.frame, 0, 255)

f1.save_image("C:\\Users\\windwoz\\Desktop\\workspace\\broken\\merged\\uh_overwrite.png")