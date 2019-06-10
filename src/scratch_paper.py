from wrappers.frame import Frame

# f1 = Frame()
# f1.load_from_string("C:\\Users\\windwoz\\Desktop\\workspace\\violetfade\\100\\frame20.png")
#
# f2 = Frame()
# f2.load_from_string("C:\\Users\\windwoz\\Desktop\\workspace\\violetfade\\100\\frame21.png")
#


# f1 = Frame()
#
# f1.load_from_string("C:\\Users\\windwoz\\Desktop\\workspace\\violetfade\\inputs\\frame30.jpg")
#
# f1.fade_block(0, 0, 100, -100)
#
# f1.save_image("C:\\Users\\windwoz\\Desktop\\workspace\\violetfade\\lmfao.jpg")

f1 = Frame()

f1.load_from_string("C:\\Users\\windwoz\\Desktop\\pythonreleases\\0.7.3\\demo_folder\\d2xtest_60_2\\comp_min\\30.png")


f2 = Frame()

f2.load_from_string("C:\\Users\\windwoz\\Desktop\\pythonreleases\\0.7.3\\demo_folder\\d2xtest_60_2\\comp_min\\true.png")


print(f1.mean(f2))