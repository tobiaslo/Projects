import pygame
import brettClass
import time


for _ in range(0, 1):
	bredde = 500
	hoyde = 500
	
	spillListe = []
	høyScore = 0
	nestScore = 0
	høyIndex = None
	nestIndex = None

	spillListe.append(brettClass.Brett(int(bredde / 10), int(hoyde / 10), 1))
	spillListe.append(brettClass.Brett(int(bredde / 10), int(hoyde / 10), 2))
	for _ in range(0, 1):
		spillListe.append(brettClass.Brett(int(bredde / 10), int(hoyde / 10), 0))
	
	for spill in spillListe:
	
		pygame.init()
		bredde = 500
		hoyde = 500
		screen = pygame.display.set_mode((bredde, hoyde))
		done = False
		clock = pygame.time.Clock()
		retning = "hoyre"
		
		def koordinatsystem(plassering):
			return plassering * 10
		
		
		while not done:
			for event in pygame.event.get():
				if event.type == pygame.QUIT:
					done = True
		
			spill.bevege()
			if spill.erIkkeOK():
				done = True
		
			screen.fill((255, 255, 255))
			liste = spill.tegne()
			for rad in range(0, len(liste)):
				for kol in range(0, len(liste[0])):
					if liste[rad][kol].hentStatus() == 1:
						pygame.draw.rect(screen, (0, 0, 0), (koordinatsystem(kol), koordinatsystem(rad), 10, 10))
					elif liste[rad][kol].hentStatus() == 0:
						pygame.draw.rect(screen, (150, 150, 150), (koordinatsystem(kol) + 1 , koordinatsystem(rad) + 1, 8, 8))
					elif liste[rad][kol].hentStatus() == 100:
						pygame.draw.rect(screen, (0, 0, 200), (koordinatsystem(kol) + 1 , koordinatsystem(rad) + 1, 8, 8))
					elif liste[rad][kol].hentStatus() == - 1:
						pygame.draw.rect(screen, (255, 0, 0), (koordinatsystem(kol) + 1 , koordinatsystem(rad) + 1, 10, 10))
					else:
						print("ups")
		
			pygame.display.flip()
		
			clock.tick(30)

		print(spill.getScore())

	print("")
	print("")
	print("Ny runde")

print("Ferdig")















