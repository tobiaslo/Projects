En versjon av det klassiske spillet Snake. i Denne implementasjonen er det et nevralt nettverk som styrer.
Ble laget 1. semester uten så mye kunnskap og ønsket i stor grad å finne ut av ting selv. Har dermed implementert det nevrale nettverket og læringen selv. En konsekvens av dette er at den er langt fra perfekt, men det var et morsomt prosjekt.

Regler:
- 100 peong per eple/firkant slangen kommer over 
- 1 poeng per trekk hvor slangen fortsatt er i livet
- Har slangen ikke kommet over noen epler/tøde firkanter vil slangen automatisk dø etter 250 trekk, dersom den ikke kommer over flere epler/ røde firkanter.
- Dersom slangen har kommet over noen epler/røde firkanter vil slangen dø etter 500 trekk, dersom den ikke kommer over flere epler/ røde firkanter.

Kjøringskrav:
- Python 3
- pygame

Manual:
1. Kjør spill.py for å se utgangspunktet (Alle vektene i det nevrale nettverket er satt til 0). Programmet vil vise tre kjøringer hver gang spill.py kjøres. Den første er vektene til det beste nevrle nettverket som er funnet. Den andre kjøringen er den nest beste, og den tredje er en variasjon på de beste vektene.
2. Kjør train.py (Dersom man ønsker, kan man kjøre train.py flere ganger for å trene mer)
3. Kjør spill.py en gang til for å se resultatet.
