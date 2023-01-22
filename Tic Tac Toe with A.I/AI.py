from random import randint
cross = 'X'
nought = 'O'

def place_piece(board,best_move,ai_piece):
    board[best_move[0]][best_move[1]] = ai_piece
    return board

def check_draw(board):
    for row in range(3):
        for col in range(3):
            if board[row][col] == 0:
                return False

    return True

def choose_best_move(candidate_moves):
    best_move_pos = candidate_moves[randint(0, len(candidate_moves) - 1)]
    return best_move_pos


def check_win(board):
    global cross,nought

    if (board[0][0] == cross and board[0][1] == cross and board[0][2] == cross) or \
    (board[1][0] == cross and board[1][1] == cross and board[1][2] == cross) or \
    (board[2][0] == cross and board[2][1] == cross and board[2][2] == cross) or \
    (board[0][0] == cross and board[1][0] == cross and board[2][0] == cross) or \
    (board[0][1] == cross and board[1][1] == cross and board[2][1] == cross) or \
    (board[0][2] == cross and board[1][2] == cross and board[2][2] == cross) or \
    (board[0][0] == cross and board[1][1] == cross and board[2][2] == cross) or \
    (board[2][0] == cross and board[1][1] == cross and board[0][2] == cross):
        return cross

    if (board[0][0] == nought and board[0][1] == nought and board[0][2] == nought) or \
    (board[1][0] == nought and board[1][1] == nought and board[1][2] == nought) or \
    (board[2][0] == nought and board[2][1] == nought and board[2][2] == nought) or \
    (board[0][0] == nought and board[1][0] == nought and board[2][0] == nought) or \
    (board[0][1] == nought and board[1][1] == nought and board[2][1] == nought) or \
    (board[0][2] == nought and board[1][2] == nought and board[2][2] == nought) or \
    (board[0][0] == nought and board[1][1] == nought and board[2][2] == nought) or \
    (board[2][0] == nought and board[1][1] == nought and board[0][2] == nought):
        return nought

    return False

def ai_move(board,player_piece,ai_piece):
    global cross,nought
    best_move_score = -1000
    candidate_moves = []

    for row in range(3):
        for col in range(3):
            if board[row][col] == 0:
                board[row][col] = ai_piece
                score = minimax(board,False,player_piece,ai_piece)
                board[row][col] = 0
                if score > best_move_score:
                    candidate_moves = []
                    best_move_score = score
                if score >= best_move_score:
                    candidate_best_move_pos = row,col
                    candidate_moves.append(candidate_best_move_pos)

    if len(candidate_moves) == 1:
        best_move_pos = candidate_moves[0]
    else:
        best_move_pos = choose_best_move(candidate_moves)

    return best_move_pos

def minimax(board,isMaximising,player_piece,ai_piece):
    if check_win(board) == ai_piece:
        return 100
    if check_win(board) == player_piece:
        return -100
    if check_draw(board) == True:
        return 0

    #game not finished
    if isMaximising == True:
        best_move_score = -1000
        for row in range(3):
            for col in range(3):
                if board[row][col] == 0:
                    board[row][col] = ai_piece #updating board
                    score = minimax(board,False,player_piece,ai_piece)
                    board[row][col] = 0 #undo board change
                    if score > best_move_score:
                        best_move_score = score

    else:
        best_move_score = 1000
        for row in range(3):
            for col in range(3):
                if board[row][col] == 0:
                    board[row][col] = player_piece
                    score = minimax(board,True,player_piece,ai_piece)
                    board[row][col] = 0
                    if score < best_move_score:
                        best_move_score = score

    return best_move_score

def ai(board,player_piece,check_state):
    if player_piece == cross:
        ai_piece = nought
    else:
        ai_piece = cross
    if check_state == True:
        state = check_win(board)
        if state == player_piece:
            return 'player wins'
        elif state == ai_piece:
            return 'computer wins'
        elif check_draw(board) == True:
            return 'draw'
        else:
            return 'ongoing'
    best_move = ai_move(board,player_piece,ai_piece)
    board = place_piece(board,best_move,ai_piece)
    return board,best_move