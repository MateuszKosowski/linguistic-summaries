# Podstawy Logiki Rozmytej - Notatki do Projektu

## 1. Przestrzeń rozważań (Universe of Discourse)
Zbiór wszystkiego, czego tyczy się temat. Dzieli się na dwa typy:

*   **Przestrzeń rozważań dyskretna** – to przestrzeń, w której wszystkie elementy są od siebie wyraźnie oddzielone. W naszym zadaniu jest to zbiór wszystkich fizycznych samochodów w bazie - tzn. między samochodem nr 37 i 38 nie ma samochodu 37,5.
*   **Przestrzeń rozważań gęsta (ciągła)** – to przestrzeń, gdzie między dwiema dowolnymi wartościami zawsze można wcisnąć nieskończenie wiele kolejnych wartości. Są to zakresy liczb (wartości atrybutów). Np. długość auta: jak jakieś ma 400 cm a inne 401 cm, to przy precyzyjnym pomiarze pewnie znajdzie się jakieś 400,5 cm.

---

## 2. Zbiory klasyczne (Crisp Sets)
To zbiór bez "odcieni szarości" z twardym warunkiem. Np. ustalmy, że "duża moc" to absolutnie sztywne od 170 KM w górę.
*   Dla liczby 169.9 przynależność wynosi `0`.
*   Dla liczby 170.0 przynależność wynosi `1`.
*   Dla liczby 500.0 przynależność wynosi `1`.

### Operacje na zbiorach klasycznych:
*   **Dopełnienie (NOT):** Odwrócenie wyniku. Skoro `1` to "duża moc", to "NIE duża moc" to `0`.
*   **Iloczyn (AND):** Gdy mamy co najmniej 2 warunki, czyli np. *Ciężki >= 1500kg* ORAZ *Mocny >= 170KM*, to robimy logiczny AND (oba warunki muszą być spełnione).
*   **Suma (OR):** Podobnie jak iloczyn, tylko tutaj robimy OR (wystarczy, że jeden warunek jest spełniony).

---

## 3. Zbiory rozmyte (Fuzzy Sets)
To zbiór, w którym przynależność nie jest twarda (`0` lub `1`), lecz jest ułamkiem z przedziału **od 0.0 do 1.0**. Oznacza to, że dany element może należeć do zbioru tylko w pewnym stopniu. Wartość ta jest wyliczana przez tzw. **Funkcję Przynależności** (czyli kształt wykresu, np. trójkąt lub trapez).

### Operacje na zbiorach rozmytych:

*   **Dopełnienie:** Wzór to `1.0 - wartość`.
    > *Przykład:* 210 KM to "duża moc"? Powiedzmy, że na `0.7`. Zatem "NIE duża moc" wynosi `1.0 - 0.7 = 0.3`.

*   **Iloczyn (AND):** Wzór to `MIN(wartość A, wartość B)`.
    > *Jak to rozumieć?* Mówisz do sprzedawcy: *"Panie, szukam auta. Ma być SZYBKIE i ma być TANIE"*. Porsche może mieć w "tanie" `0.01`. Wtedy szybkość cię już nie obchodzi, bo cię nie stać (najsłabsze ogniwo to 0.01). Jest to rygorystyczne przeszukiwanie, gdzie każda wada ma znaczenie. Oceniamy po najsłabszej cesze.

*   **Suma (OR):** Wzór to `MAX(wartość A, wartość B)`.
    > *Jak to rozumieć?* Sytuacja odwrotna niż w iloczynie. Mówisz: *"Niech będzie SZYBKIE, a jak nie, to niech będzie chociaż TANIE"*. Skupiamy się na największym pozytywie. Nawet jeśli auto jest powolne (`0.1`), ale super tanie (`0.98`), to wynik wynosi `0.98` i bierzemy je z pocałowaniem ręki.
    
## 4. Właściwości zbioru rozmytego

### Wysokość zbioru rozmytego
To po prostu **największa wartość przynależności**, jaką udało się osiągnąć w całym naszym zbiorze. Szukamy "rekordzisty".
> *Przykład:* Szukamy w bazie aut "Bardzo Szybkich" (prędkość > 300 km/h daje 1.0). Przepuszczamy przez funkcję wszystkie 10 000 aut z bazy. Okazuje się, że najszybsze auto w naszej bazie ma "tylko" 250 km/h, co daje mu przynależność 0.7. Nikt nie osiągnął wyższego wyniku.
> **Wysokość tego zbioru w naszej bazie wynosi 0.7.**

### Zbiór rozmyty normalny
Zbiór jest normalny, jeśli jego wysokość wynosi **dokładnie 1.0**.
Oznacza to, że w naszej przestrzeni rozważań istnieje *przynajmniej jeden* element, który w 100% idealnie pasuje do definicji.
> *Przykład:* Szukamy "Taniego auta". W bazie mamy zardzewiałego Fiata, który idealnie pasuje do definicji taniości i dostaje ocenę 1.0. Ponieważ maksymalny wynik to 1.0, zbiór jest normalny. *(W praktyce, projektując program, staramy się, aby wszystkie nasze etykiety były zbiorami normalnymi).*

### Zbiór rozmyty pusty
Zbiór jest pusty, jeśli jego wysokość wynosi **dokładnie 0.0**.
Oznacza to, że absolutnie żaden element w bazie nie pasuje do definicji nawet w ułamku procenta.
> *Przykład:* Zdefiniowany zbiór "Pojazd Kosmiczny" (prędkość > 20 000 km/h). Przepuszczamy przez niego wszystkie samochody z bazy. Każdy jeden dostaje ocenę 0.0. Zbiór jest pusty.

### Zbiór rozmyty wypukły
To pojęcie dotyczy kształtu samego wykresu funkcji przynależności. Zbiór jest wypukły, jeśli jego wykres ma tylko "jeden szczyt" (lub płaskodenną górę) i **nie ma żadnych "dolin" w środku**.
> *Jak to rozumieć?* Jeśli jest wykres trójkąta – to jest zbiór wypukły. Trapezu (rośnie, jest płasko na szczycie, spada) – też wypukły. Ale istnieją funkcje, które mają dwa szczyty (np. rośnie, spada, a potem znowu rośnie) – to już nie jest wypukły zbiór. 

## 5. Funkcje przynależności

Funkcja przynależności to matematyczny wzór (wykres), który przelicza surową wartość z bazy danych (np. `190 KM`) na ułamek od `0.0` do `1.0`. W zależności od tego, jakiego słowa (etykiety) używamy, dobieramy inny kształt funkcji.
### 1. Funkcja trójkątna (Triangular)
Ma 3 parametrów: **a** (początek), **b** (szczyt), **c** (koniec).
*   **Zastosowanie:** Dobra dla słów typu "Średni", gdzie istnieje jedna, idealna wartość w punkt.
*   **Przykład:** Etykieta *"Poziom spalania - średnie"*. Parametry: `a=6`, `b=8`, `c=10`.
    *   Auto pali 6 litrów i mniej -> ocena `0.0`.
    *   Auto pali idealnie 8 litrów -> ocena `1.0` (sam szczyt trójkąta).
    *   Auto pali 7 litrów -> trafia na zbocze góry, w połowie drogi, więc ocena `0.5`.
    *   Auto pali 10 litrów i więcej -> ocena `0.0`.
    
### 2. Funkcja trapezoidalna
Wykres ma kształt trapezu (rośnie, osiąga szczyt, **utrzymuje się**, po czym spada).
4 parametry: **a** (początek), **b** (początek płaskiego szczytu), **c** (koniec płaskiego szczytu), **d** (koniec).

*   **Zastosowanie:** Opisywanie przedziałów, gdzie nie ma jednej idealnej wartości
*   **Przykład:** Etykieta *"Duży bagażnik"*. Parametry: `a=350`, `b=450`, `c=600`, `d=700`.
    *   Bagażnik ma 300 litrów -> ocena `0.0`.
    *   Bagażnik ma 400 litrów -> zbocze, ocena np. `0.5`.
    *   Bagażniki 450 l, 500 l, 550 l -> wszystkie dostają ocenę `1.0`.

> **Trapez Połówkowy:**
> Funkcji trapezowej bardzo często używa się do definiowania skrajności (np. *"Bardzo szybki"*). Zamiast zamykać trapez, zostawiamy go otwartego w nieskończoność.

### 3. Funkcja Gaussowska
Wykres ma kształt gładkiej górki. Nie ma tu ostrych kantów jak w trójkącie czy trapezie.
Potrzebujemy 2 parametrów: **środek** (miejsce gdzie jest 1.0) oraz **odchylenie** (jak jest "gruba" i szeroka).

*   **Zastosowanie:** Gdy chcemy, aby przynależność do zbioru malała płynnie i nigdy nie "odcinała" danych w sposób nagły. Funkcja Gaussa dąży do zera, ale matematycznie nigdy go nie osiąga.
*   **Przykład:** Etykieta *"Miejski samochód"* (określana po długości `length_mm`). Długość idealna to 3800 mm (`1.0`). Auto o długości 4000 mm dostaje ocenę `0.7`, 4200 mm ocenę `0.2`, a wielki pickup 5500 mm ocenę `0.0001`. W przeciwieństwie do trójkąta, nie odcięliśmy pickupa grubą kreską, po prostu daliśmy mu bardzo niską notę.

## 6. Budowanie zdań

### 1. Zmienna lingwistyczna 
To po prostu cecha (atrybut), którą badamy np: "Moc silnika"

### 2. Etykiety 
"Słowa", które może przyjmować zmienna lingwistyczna. Każda etykieta to osobny zbiór rozmyty z przypisaną własną funkcją przynależności.
> *Przykład dla zmiennej "Moc silnika":*
> *   Etykieta 1: **"Słaby"** (Trapez od 0 do 100 KM)
> *   Etykieta 2: **"Średni"** (Trójkąt ze szczytem w 130 KM)
> *   Etykieta 3: **"Mocny"** (Trapez od 170 KM w górę)

### 3. Kwantyfikatory rozmyte
Odpowiadają na pytanie **"ILE?"**. Dzielą się na dwa rodzaje:

#### A) Kwantyfikator absolutny
Odpowiada na pytanie o **konkretną liczbę sztuk**. Określa się go na przestrzeni ilości obiektów (w  naszym przypadku aut).
> *Przykłady słów:* "Około sto", "Mniej niż tysiąc"
> *Jak działa?* Trójkąt ma szczyt w liczbie 100. Jeśli program znajdzie w bazie 100 pasujących aut, to kwantyfikator "Około sto" dostaje ocenę `1.0`. Jak znajdzie 90 aut, dostaje `0.8`.

#### B) Kwantyfikator względny
Odpowiada na pytanie o **procent (ułamek, proporcję)**. Określa się go na przestrzeni od `0.0` do `1.0`.
> *Przykłady słów:* "Większość", "Około połowa", "Prawie żadne", "Znaczna mniejszość".
> *Jak działa?* Trójkąt ma szczyt w `0.5` (50%) dla słowa "Około połowa". Jeśli program wyliczy, że pasujące auta stanowią 52% wszystkich aut w bazie, to ten kwantyfikator dostaje ocenę np. `0.95`.

Czyli tak podsumowaując: etykieta opisuje konkretną cechę (np. "Szybki"), a kwantyfikator mówi nam, ile aut spełnia tę cechę (np. "Większość"). Dzięki temu możemy budować zdania typu: *"Większość aut w bazie jest SZYBKA"* i ocenić, jak bardzo to zdanie jest prawdziwe.

## 7. Sumaryzator i Kwalifikator

Zdanie bazowe wygląda tak:
> **[Kwantyfikator]** `(kwalifikator / podmiot)` ma/jest **[Sumaryzator]**.

### 1. Kwalifikator (Qualifier) - filtr wstępny
Kwalifikator to zbiór rozmyty, który pełni rolę "podmiotu" zdania. Służy do **zawężenia grupy obiektów**, które nas interesują. Działa jak rozmyty odpowiednik klauzuli `WHERE` w SQL.
> *Przykład:* "Większość **ciężkich aut** ma dużą moc."
> Kwalifikatorem jest tu zbiór rozmyty "Ciężki" (waga > 1500 kg). Obliczenia nie będą dotyczyć wszystkich aut.

### 2. Sumaryzator (Summarizer) - badana cecha
Sumaryzator to zbiór rozmyty, który jest **główną cechą, którą chcemy sprawdzić/udowodnić** u wyselekcjonowanej wyżej grupy aut.
> *Przykład:* "Większość ciężkich aut ma **dużą moc**."
> Sumaryzatorem jest tu zbiór rozmyty "Duża moc" (konie mechaniczne > 180 KM).

---

### Formy Sumaryzatorów i Kwalifikatorów

Słowa "Ciężki" lub "Duża moc" to tzw. **formy proste**. Ale w języku rzadko mówimy tak krótko. Często łączymy cechy używając spójników (AND / OR). Wtedy mówimy o **formach złożonych**.

#### A) Forma prosta (Simple)
Zbudowana tylko z jednej etykiety (zmiennej).
*   *Prosty kwalifikator:* "Większość **starych aut**..." (Tylko wiek).
*   *Prosty sumaryzator:* "...ma **duże spalanie**." (Tylko spalanie).

#### B) Forma złożona za pomocą spójnika (Iloczyn / AND)
Łączymy dwa lub więcej zbiorów, licząc **Minimum** z ich wartości.
*   *Złożony kwalifikator:* "Większość **[starych I tanich]** aut..."
    > (Program bierze wartość wieku `0.8`, wartość ceny `0.3` i zwraca najsłabsze ogniwo -> kwalifikator wynosi `0.3`).
*   *Złożony sumaryzator:* "...ma **[dużą moc I wysokie spalanie]**."
    > (Program bierze wartość mocy `0.9` i spalania `0.7` -> sumaryzator wynosi `0.7`).

#### C) Forma złożona za pomocą spójnika "LUB" (Suma / OR)
Łączymy dwa lub więcej zbiorów, licząc **Maksimum** z ich wartości.
*   *Złożony sumaryzator:* "Większość aut jest **[szybka LUB tania]**".
    > (Szukamy jakiegoś usprawiedliwienia. Auto powolne `0.0`, ale tanie `0.8` -> sumaryzator dla tego auta wynosi `0.8`).