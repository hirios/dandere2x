//
// Created by https://github.com/CardinalPanda
//
//Licensed under the GNU General Public License Version 3 (GNU GPL v3),
//    available at: https://www.gnu.org/licenses/gpl-3.0.txt

#ifndef DANDERE2X_DIFFERENCEBLOCKS_H
#define DANDERE2X_DIFFERENCEBLOCKS_H

#include <vector>
#include "BlockMatch/Block.h"

class DifferenceBlocks {

public:
    int size;

    int xMax;
    int yMax; //yMax is never used, but left in for readability.

    int xCount;
    int yCount;
    std::vector<Block> list = std::vector<Block>();

    DifferenceBlocks(int xDimension, int yDimension, int size) {
        this->xMax = xDimension / size;
        this->yMax = yDimension / size;
        this->xCount = 0;
        this->yCount = 0;
        this->size = size;
    }

    // Fill in a grid going left to right, then
    // goto the next row whenever we finish a row.
    void add(int x, int y) {
        size++;
        if (xCount + 1 < xMax) {
            xCount++;
            list.push_back(Block(x, y, xCount, yCount, 9999));
        } else {
            yCount++;
            xCount = 0;
            list.push_back(Block(x, y, xCount, yCount, 9999));

        }
    }
};

#endif //DANDERE2X_DIFFERENCEBLOCKS_H
