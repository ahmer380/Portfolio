def find_empty_square(board):
    for col in range(len(board)):
        for row in range(len(board[0])):
            if board[col][row] == 0:
                return (col,row)

    return None


def solve(board):
    find = find_empty_square(board)
    if not find:
        return True
    else:
        row,col = find

    for number in range(1,10):
        if valid(board,number,(row,col)) == True:
            board[row][col] = number

            if solve(board) == True:
                return True

            board[row][col] = 0

    return False


def valid(board,number,pos):
    for row in range(len(board[0])):
        if board[pos[0]][row] == number and pos[1] != row:
            return False

    for col in range(len(board)):
        if board[col][pos[1]] == number and pos[0] != col:
            return False

    box_x = pos[1] // 3
    box_y = pos[0] // 3

    for row in range(box_y * 3,box_y * 3 + 3):
        for col in range(box_x * 3, box_x * 3 + 3):
            if board[row][col] == number and (row,col) != pos:
                return False

    return True