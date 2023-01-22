from Algorithm import solve
import pygame, sys

pygame.init()
pygame.display.set_caption('Sudoku Solver')
cell_size = 80
cell_number = 9
screen = pygame.display.set_mode((cell_size*cell_number,cell_size*cell_number))
string_font = pygame.font.Font(None,30)
number_font = pygame.font.Font(None,50)
show_numbers = []
number_positions = []
clock = pygame.time.Clock()

RED = (255,99,71)
GREEN = (62,180,137)
GREY = (220,220,220)
BLACK = (0,0,0)
WHITE = (255,255,255)


class draw:
    def __init__(self):
        self.board = [
            [0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0],
        ]

    def edit_board(self,clicked_col,clicked_row,number):
        self.board[clicked_row][clicked_col] = number

    def solve_board(self):
        solve(self.board)
        new_board = self.board
        return new_board

    def draw_canvas(self):
        for line in range(cell_number):
            line_vertical = pygame.Rect(line*cell_size,0,5,cell_number*cell_size)
            line_horizontal = pygame.Rect(0, line * cell_size, cell_number * cell_size , 5)
            pygame.draw.rect(screen, GREY, line_vertical)
            pygame.draw.rect(screen, GREY, line_horizontal)

        for line in range(cell_number):
            if line % 3 == 0:
                line_vertical = pygame.Rect(line * cell_size, 0, 5, cell_number * cell_size)
                line_horizontal = pygame.Rect(0, line * cell_size, cell_number * cell_size, 5)
                pygame.draw.rect(screen, BLACK, line_vertical)
                pygame.draw.rect(screen, BLACK, line_horizontal)

def get_col_row_from_pos(x_pos,y_pos):
    clicked_col = x_pos // cell_size
    clicked_row = y_pos // cell_size
    return clicked_col,clicked_row

def display_enter_box(pos):
    x_pos = pos[0]
    y_pos = pos[1]
    clicked_col,clicked_row = get_col_row_from_pos(x_pos,y_pos)
    text = 'Enter:'
    text_surface = string_font.render(text,True,RED)
    text_x = (cell_size * clicked_col) + cell_size // 2
    text_y = (cell_size * clicked_row) + cell_size // 2
    text_rect = text_surface.get_rect(center = (text_x,text_y))
    screen.blit(text_surface,text_rect)
    pygame.display.update()
    return text_x,text_y

def invalid_number():
    print('Try again')

def display_submit_box():
    text = 'submit'
    x_pos = (cell_size * 8) + cell_size // 2
    y_pos = cell_size // 4

    text_surface = string_font.render(text,True,GREEN)
    text_rect = text_surface.get_rect(center = (x_pos,y_pos))
    bg_rect = pygame.Rect(cell_size * 8, 5, text_rect.width + 10, text_rect.height+5)
    pygame.draw.rect(screen, GREY, bg_rect)
    screen.blit(text_surface,text_rect)

def ask_number(pos):
    number_received = False
    while number_received == False:
        text_x,text_y = display_enter_box(pos)

        input_entered = False
        while input_entered == False:
            for event in pygame.event.get():
                if event.type == pygame.KEYDOWN:
                    number = event.unicode
                    input_entered = True

        try:
            number = int(number)
            if number < 10 and number >= 0:
                if number == 0:
                    print('going back')
                else:
                    print('accepted')
                number_received = True
                return number,text_x,text_y
            else:
                invalid_number()
        except:
            invalid_number()

def generate_display_number(number,text_x,text_y,colour):
    number_surface = number_font.render(str(number),True,colour)
    number_rect = number_surface.get_rect(center = (text_x,text_y))
    return number_surface,number_rect

def draw_elements():
    for number in show_numbers:
        screen.blit(number[0], number[1])

    draw.draw_canvas()
    display_submit_box()
    pygame.display.update()

def check_repeat(clicked_pos,number):
    for value in number_positions:
        if clicked_pos == value:
            show_numbers.pop(number_positions.index(value))
            number_positions.remove(value)

    if number != 0:
        number_positions.append(clicked_pos)

def solution_position(position):
    for value in number_positions:
        if position == value:
            return False

    return True

def initiate_mouse_down():
    clicked_pos = (clicked_col, clicked_row) = get_col_row_from_pos(pos[0], pos[1])
    number, text_x, text_y = ask_number(pos)
    properties = (number_surface, number_rect) = generate_display_number(number, text_x, text_y,(255,99,71))
    check_repeat(clicked_pos,number)
    if number != 0:
        show_numbers.append(properties)
    draw.edit_board(clicked_col,clicked_row,number)

def initiate_answer():
    new_board = draw.solve_board()
    for line in new_board:
        print(line)
    for row in range(len(new_board)):
        for col in range(len(new_board[0])):
            number = new_board[row][col]
            position = (col,row)
            x_pos = (cell_size * col) + cell_size // 2
            y_pos = (cell_size * row) + cell_size // 2
            if solution_position(position) == True:
                properties = generate_display_number(number,x_pos,y_pos,GREEN)
                show_numbers.append(properties)

draw = draw()

while True:
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            pygame.quit()
            sys.exit()

        if event.type == pygame.MOUSEBUTTONDOWN:
            pos = pygame.mouse.get_pos()
            if pos[0] >= cell_size * 8 and pos[1] <= cell_size // 4:
                initiate_answer()
            else:
                initiate_mouse_down()

    screen.fill(WHITE)
    draw_elements()
    clock.tick(60)