# Model

![Model](https://github.com/klimesf/diploma-thesis-images/blob/master/class-diagrams/example-model.png?raw=true)

- Uživatel
  - identifikátor
  - jméno
  - emailová adresa
  - role
- Produkt
  - identifikátor
  - jméno
  - nákupní cena
  - prodejní cena
  - počet kusů na skladu
- Objednávka
  - identifikátor
  - doručovací adresa
    - číslo popisné
    - ulice
    - město
    - psč
  - fakturační adresa
    - číslo popisné
    - ulice
    - město
    - psč
  - seznam položek
- Položka objednávky
  - produkt
  - objednávka
  - počet kusů

# Use-cases
- (UC01) Nepřihlášený uživatel si může vytvořit zákaznícký účet
- (UC02) Zákazník může prohlížet produkty
- (UC03) Zákazník může vkládat produkty do košíku
- (UC04) Zákazník může vytvořit objednávku
- (UC05) Skladník si může prohlížet produkty
- (UC06) Skladník může do systému zadávat nové produkty
- (UC07) Skladník může upravovat u produktů stav skladových zásob
- (UC08) Skladník si může zobrazovat objednávky
- (UC09) SKladník může upravovat stav objednávek
- (UC10) Administrátor si může prohlížet objednávky
- (UC11) Administrátor může upravovat cenu produktů
- (UC12) Administrátor může vytvářet uživatele (skladníky)
- (UC13) Administrátor může mazat uživatele (skladníky i zákazníky)

K tomu se budou vázat business rules:
- (BR01 : UC01) - Uživatel nesmí být přihlášený (precondition)
- (BR02 : UC02 + UC03) - Uživatel nesmí zobrazovat ani manipulovat s produkty, které nejsou aktivní (postcondition - filtrování)
- (BR03 : UC02 až UC04) - Uživatel nesmí u produktu vidět marži, pouze výslednou cenu (postcondition - filtrování)
- (BR04 : UC04) - Uživatel musí řádně vyplnit doručovací adresu (č.p., ulice, město, PSČ, stát) (precondition)
- (BR05 : UC04) - Uživatel musí řádně vyplnit fakturační adresu (č.p., ulice, město, PSČ, stát) (precondition)
[BR04 i BR05 lze rozpadnout každé na 5 dílčích pravidel, tedy č.p. musí mít nějaký tvar, ulice, město i stát musí mít nenulovou délku, PSČ musí být 5 čísel]
- (BR06 : UC01 + UC04) - Zákazník musí mít vyplněnou emailovou adresu (precondition)
- (BR07 : UC04) - Položky objednávky musí mít počet kusů větší než 0 (precondition)
- (BR08 : UC04) - Položky objednávky musí mít počet kusů menší než je aktuální stav skladových zásob produktu (precondition)
- (BR09 : UC04) - Stát musí být v seznamu zemí, do kterých firma doručuje (precondition)
- (BR10 : UC04) - Zákazník musí být přihlášen (precondition)
- (BR11 : UC05 až UC09) - Skladník musí být do systému přihlášen a mít roli "Skladník" (precondition)
- (BR12 : UC05) - Skladník u produktu nesmí vidět marži, pouze výslednou cenu (postcondition)
- (BR13 : UC06) - Produkt musí mít jméno s délkou >5 (precondition)
- (BR14 : UC07) - Stav zásob produktů musí být číslo větší nebo rovno 0 (precondition)
- (BR15 : UC08) - Skladník nesmí vidět celkový součet cen objednávek (postcondition)
- (BR16 : UC09) - Stav objednávky musí být pouze "přijato", "expedováno" a "doručeno" (precondition)
- (BR17 : UC10 až UC13) - Administrátor musí být do systému přihlášen a mít roli "Administrátor" (precondition)
- (BR18 : UC11) - Výsledná cena produktu musí být větší než jeho nákupní cena (precondition)
- (BR19 : UC12) - Skladník musí mít jméno delší než 2 znaky (precondition)
- (BR20 : UC12) - Skladník musí mít emailovou adresu v platném formátu (precondition)
- (BR21 : UC13) - Smazaný uživatel nesmí mít žádné objednávky ve stavu "přijato" nebo "expedováno" (precondition)  

# Komponenty systému

![Components](https://github.com/klimesf/diploma-thesis-images/blob/master/component-diagrams/example-system.png?raw=true)

- Product
- User
- Billing
- Shipping
- Mailing
- Order
- Webové rozhraní (UI)

# Sdílená business pravidla

![Hirearchy](https://github.com/klimesf/diploma-thesis-images/blob/master/misc/example-system-context-hierarchy.png?raw=true)

- Product definuje BR02, ale aplikuje se v UI
- Shipping definuje BR04 a BR09, ale aplikuje se v Order a v UI
- Billing definuje BR05, ale aplikuje se v Order a v UI
- Mailing definuje BR06, ale aplikuje se v User, Order a v UI

atd., myslím že se tam toho dá vymyslet spoustu.

# Join-points

![Join-points](https://github.com/klimesf/diploma-thesis-images/blob/master/activity-diagrams/join-points.png?raw=true)

# Aspect weaving

![Weaving](https://github.com/klimesf/diploma-thesis-images/blob/master/activity-diagrams/business-rules-weaver.png?raw=true)

# Načítání business pravidel

![Business rules loading](https://github.com/klimesf/diploma-thesis-images/blob/master/activity-diagrams/business-context-loading.png?raw=true)

# Administrace business pravidel

![Business rules administration](https://github.com/klimesf/diploma-thesis-images/blob/master/activity-diagrams/business-context-management.png?raw=true)
![Business rules administration](https://github.com/klimesf/diploma-thesis-images/blob/master/sequence-diagrams/business-context-administration.png?raw=true)
![Business rules administration](https://github.com/klimesf/diploma-thesis-images/blob/master/sequence-diagrams/business-context-administration-update.png?raw=true)

# Business Context Metamodel

![Business Context Metamodel](https://github.com/klimesf/diploma-thesis-images/blob/master/class-diagrams/business-context-metamodel.png) 
