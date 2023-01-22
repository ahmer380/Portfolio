import pygame, sys
from AI import ai
from random import randint
pygame.init()
pygame.display.set_caption('Tic Tac Toe')
cell_size = 180
cell_number = 3
screen = pygame.display.set_mode((cell_size*cell_number + 400,cell_size*cell_number + 6))
clock = pygame.time.Clock()
turn_count = 0
show = []
clicked_pos = 0,0
player_piece = False
ai_piece = False
ask_confirm_move = False
ask_confirm_piece = False
game_end = False

WHITE = (255,255,255)
GREEN = (62,180,137)
BLACK = (0,0,0)
RED = (194,24,7)
YELLOW = (212,175,55)

class board:
    def __init__(self):
        self.board = [
            [0, 0, 0],
            [0, 0, 0],
            [0, 0, 0],

        ]

    def edit_board(self,clicked_pos,player_piece):
        self.board[clicked_pos[1]][clicked_pos[0]] = player_piece

    def presence_check(self,clicked_pos):
        if self.board[clicked_pos[1]][clicked_pos[0]] == 0: #empty
            return True

    def algorithm(self):
        state = ai(self.board,player_piece,True)
        if state != 'ongoing':
            return state,False
        self.board,best_move = ai(self.board,player_piece,False)
        state = ai(self.board, player_piece, True)
        return state,best_move

def draw_canvas():
    for line in range(cell_number + 1):
        line_vertical = pygame.Rect(line*cell_size,0,5,cell_number*cell_size + 5)
        line_horizontal = pygame.Rect(0, line * cell_size, cell_number * cell_size , 5)
        pygame.draw.rect(screen, BLACK, line_vertical)
        pygame.draw.rect(screen, BLACK, line_horizontal)

def draw_elements():
    for item in show:
        screen.blit(item[0], item[1])

    draw_canvas()
    pygame.display.update()

def get_col_row_from_pos(x_pos,y_pos):
    clicked_col = x_pos // cell_size
    clicked_row = y_pos // cell_size
    return clicked_col,clicked_row

def display_starting_content():
    cross = text_schematic('X',630,270,250,BLACK)
    nought = text_schematic('O',810,270,250,BLACK)
    choose = text_schematic('Choose Piece', 700, 140, 50, BLACK)
    dashed_line = text_schematic('---------------------',700,160,50,BLACK)
    show.extend([cross,nought])
    show.append(choose)
    show.append(dashed_line)

def display_result(state):
    result = text_schematic(state + '!',725,400,40,YELLOW)
    show.append(result)

def text_schematic(symbol,x_pos,y_pos,size,colour):
    font = pygame.font.Font(None, size)
    surface = font.render(str(symbol),True,colour)
    rect = surface.get_rect(center = (x_pos,y_pos))
    return surface,rect

def confirm_move_display(clicked_col,clicked_row,x_pos,y_pos):
    global ask_confirm_move
    if clicked_col > 2 or clicked_row > 2:
        return
    square = pygame.Rect(clicked_col*cell_size,clicked_row*cell_size,cell_size,cell_size)
    pygame.draw.rect(screen, GREEN, square)
    confirm = text_schematic('Confirm = 1',x_pos,y_pos,40,BLACK)
    show.append(confirm)
    ask_confirm_move = True

def confirm_move():
    global ask_confirm_move,clicked_pos
    x_pos = clicked_pos[0]*cell_size + cell_size // 2
    y_pos = clicked_pos[1]* cell_size + cell_size // 2
    input_entered = False
    while input_entered == False:
        for event in pygame.event.get():
            if event.type == pygame.KEYDOWN:
                choice = event.unicode
                input_entered = True
    screen.fill(WHITE)
    show.pop()
    if choice == '1':
        player_piece_add = text_schematic(player_piece,x_pos,y_pos,200,GREEN)
        show.append(player_piece_add)
        board.edit_board(clicked_pos,player_piece)
        state = initiate_ai_sequence(2)
        if state != 'ongoing':
            display_result(state)
    ask_confirm_move = False

def confirm_piece():
    global ask_confirm_piece,player_piece,ai_piece
    input_entered = False
    while input_entered == False:
        for event in pygame.event.get():
            if event.type == pygame.KEYDOWN:
                choice = event.unicode
                input_entered = True
    screen.fill(WHITE)
    if choice != '1':
        show.pop()
        show.pop()
        player_piece = False
        ai_piece = False
    show.pop()
    if player_piece == 'O':
        initiate_ai_sequence(1)
    ask_confirm_piece = False

def choose_player_piece(clicked_pos):
    global player_piece,ai_piece,ask_confirm_piece
    if clicked_pos == (3,1):
        player_piece = 'X'
        ai_piece = 'O'
        x_pos = 630
        other_x_pos = 810
    elif clicked_pos == (4,1):
        player_piece = 'O'
        ai_piece = 'X'
        x_pos = 810
        other_x_pos = 630
    else:
        x_pos = 0
        other_x_pos = 0 # these 2 lines holds no importance other than suppressing warnings by the IDE
    highlighted_player_piece = text_schematic(player_piece,x_pos,270,250,GREEN)
    highlighted_ai_piece = text_schematic(ai_piece,other_x_pos,270,250,RED)
    confirm = text_schematic('Confirm piece = 1',725, 400, 40, BLACK)
    show.append(highlighted_player_piece)
    show.append(highlighted_ai_piece)
    show.append(confirm)
    ask_confirm_piece = True

def initiate_ai_sequence(move_number):
    global game_end

    if move_number == 1:
        candidate_moves = [[0,0],[0,2],[2,0],[2,2],[1,1]]
        best_move = candidate_moves[randint(0,len(candidate_moves)-1)]
        board.board[best_move[0]][best_move[1]] = ai_piece
        state = 'ongoing'
    else:
        state,best_move = board.algorithm()
        if best_move == False:
            game_end = True
            return state

    best_move_x_pos = best_move[1] * cell_size + cell_size // 2
    best_move_y_pos = best_move[0] * cell_size + cell_size // 2
    ai_piece_add = text_schematic(ai_piece, best_move_x_pos, best_move_y_pos, 200, RED)
    show.append(ai_piece_add)
    if state != 'ongoing':
        game_end = True

    return state

def initiate_board_down():
    global player_piece,clicked_pos
    clicked_pos = (clicked_col, clicked_row) = get_col_row_from_pos(pos[0], pos[1])
    if (clicked_pos == (3,1) or clicked_pos == (4,1)) and player_piece == False:
        choose_player_piece(clicked_pos)
        return

    x_pos = (cell_size * clicked_col) + cell_size // 2
    y_pos = (cell_size * clicked_row) + cell_size // 2
    if player_piece != False and board.presence_check(clicked_pos) == True:
        confirm_move_display(clicked_col,clicked_row,x_pos,y_pos)

board = board()
display_starting_content()
screen.fill(WHITE)

while True:
    if ask_confirm_move == True:
        confirm_move()
    if ask_confirm_piece == True:
        confirm_piece()
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            pygame.quit()
            sys.exit()

        if event.type == pygame.MOUSEBUTTONDOWN and game_end == False:
            pos = pygame.mouse.get_pos()
            initiate_board_down()

    draw_elements()
    clock.tick(60)