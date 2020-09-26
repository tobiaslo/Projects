from spillebrett import Spillebrett
import pygame

x = int(input("Hvor mange rader ønsker du å ha? "))
y = int(input("Hvor mange kolonner ønsker du å ha? "))


def kordinatsystem(tall):
	return tall * 20 + 1

pygame.init()
size = (kordinatsystem(y)+1, kordinatsystem(x)+1)
screen = pygame.display.set_mode(size)

spill = Spillebrett(rader=x, kolonner=y)

done = False
while not done:
	for event in pygame.event.get():
		if event.type == pygame.QUIT:
			done = True

	pressed = pygame.key.get_pressed()
	if pressed[pygame.K_SPACE]:
		spill.oppdatering()
		pygame.time.wait(80)


	screen.fill((255,255,255))
	for n in range(len(spill.getBrett())):
		for i in range(len(spill.getBrett()[n])):
			if spill.getBrett()[n][i].erLevende():
				pygame.draw.rect(screen, (0,255,0), [kordinatsystem(i), kordinatsystem(n),19,19])
			elif spill.getBrett()[n][i].erLevende() == False:
				pygame.draw.rect(screen, (255,0,0), [kordinatsystem(i), kordinatsystem(n),19,19])

	pygame.display.flip()
