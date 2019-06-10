//
// Created by https://github.com/CardinalPanda
//
//Licensed under the GNU General Public License Version 3 (GNU GPL v3),
//    available at: https://www.gnu.org/licenses/gpl-3.0.txt

#include "Differences.h"
#include "DifferenceBlocks.h"


void Differences::run() {

    //flag all the pixels that were able to be copied
    flag_pixels();

    //once flagPixels is called, count how many pixels didn't get flagged
    int count = count_empty_pixels();

    //find out how many blocks are needed to redraw all the missing pixels
    int blocksNeeded = count / (block_size * block_size);

    //let this be the dimensions for our output image
    this->dimensions = (int) sqrt(blocksNeeded) + 1;

    //using the new information we have, add all the missing blocks
    //into invDifferences
    add_missing_blocks_to_differences_blocks();
}

void Differences::write(std::string output_file) {
    //create a temp file
    std::ofstream out(output_file + ".temp");

    out << block_size << std::endl;
    //write vectors to temp file
    for (int x = 0; x < difference_blocks->list.size(); x++) {
        out << difference_blocks->list[x].x_start << "\n" <<
            difference_blocks->list[x].y_start << "\n" <<
            difference_blocks->list[x].x_end << "\n" <<
            difference_blocks->list[x].y_end << std::endl;
    }
    out.close();
    rename((output_file + ".temp").c_str(), output_file.c_str());

}

//private

void Differences::flag_pixels() {
    this->occupied_pixel.resize(this->width, std::vector<bool>(this->height));

    /**
     * Flag all the pixels that already exist within predictive_frame2,
     * so we know which pixels are NOT found in predictive_frame_2
     */
    for (int outer = 0; outer < blocks.size(); outer++) {
        for (int x = 0; x < block_size; x++) {
            for (int y = 0; y < block_size; y++) {
                this->occupied_pixel[blocks[outer].x_start + x][blocks[outer].y_start + y] = true;
            }
        }
    }
}


int Differences::count_empty_pixels() {
    int count = 0;
    for (int x = 0; x < frame2->width; x++) {
        for (int y = 0; y < frame2->height; y++) {
            if (occupied_pixel[x][y] == false)
                count++;
        }
    }
    return count;
}

void Differences::add_missing_blocks_to_differences_blocks() {
    difference_blocks = std::make_unique<DifferenceBlocks>(dimensions * (block_size),
                                                           dimensions * (block_size),
                                                           block_size);

    /**
     * Going into this foor loop, we know every pixel that is missing from frame_2,
     * So we add all the blocks that are missing into a 'differences' image
     */
    for (int x = 0; x < frame2->width; x++) {
        for (int y = 0; y < frame2->height; y++) {
            if (occupied_pixel[x][y] == false) {

                difference_blocks->add(x, y); //if does not exist, add it to the 'differences' image

                //set the pixels to 'draw' so we don't redraw it twice
                for (int delta_x = 0; delta_x < block_size; delta_x++) {
                    for (int delta_y = 0; delta_y < block_size; delta_y++) {
                        occupied_pixel[x + delta_x][y + delta_y] = true;
                    }
                }
            }
        }
    }
}


