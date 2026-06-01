# Miary jakości podsumowań lingwistycznych (T1–T11)

Źródło: Niewiadomski, *Methods for the linguistic summarization of data — applications
of fuzzy sets and their extensions*, rozdział 8 (s. 152–165).

**Sama książka definiuje dokładnie 10 ponumerowanych miar jakości, T1–T10.** Końcowa
jakość („dobroć podsumowania") to ważona średnia tych miar.

**T11 poniżej to miara dodatkowa („długość kwalifikatora")** — NIE jest zdefiniowana
w książce pod tym numerem. Została tu skonstruowana przez bezpośrednią analogię do T5
(„długość podsumowania", wzór 8.56), aby uzupełnić brakującą symetryczną miarę dla
kwalifikatora `W`: sumaryzator `S` ma nieprecyzyjność (T2), kardynalność (T8) i długość
(T5); kwalifikator `W` ma nieprecyzyjność (T9) i kardynalność (T10), ale w książce nie ma
dla niego miary długości. T11 ją dostarcza.

---

## Jak czytać te miary (najważniejsza intuicja)

Każda z 11 miar zwraca liczbę z przedziału `[0,1]`, gdzie **wyżej = lepiej**, a końcowa
„dobroć" podsumowania to ich ważona średnia (domyślnie równe wagi `1/11`). Ale **nie
wszystkie miary mówią o tym samym** — i to jest klucz do zrozumienia wyników. Miary
dzielą się na trzy grupy wg tego, **ile naprawdę mówią o Twoich danych**:

1. **Miary „o danych" (merytoryczne): T1 i T3.** Tylko one zależą od faktycznej
   zawartości bazy. **T1** mówi, czy zdanie jest prawdziwe; **T3**, jak dużą część
   rekordów ono faktycznie obejmuje. To one różnicują ranking i niosą informację.
2. **Miary „o kształcie" (strukturalne): T2, T6, T7, T8.** Zależą od tego, jak
   *zaprojektowano zbiory rozmyte* (wąskie czy szerokie etykiety, jaki kwantyfikator),
   a nie od danych. Premiują zwięzłość i ostrość pojęć. Są niemal stałe między
   podsumowaniami.
3. **Miary „o formie zdania": T5, T11 (długości) oraz T4, T9, T10.** Zależą od budowy
   samego wyrażenia: T5/T11 karzą długie zdania; T4 ożywa dopiero dla sumaryzatorów
   złożonych; T9–T11 istnieją wyłącznie wtedy, gdy jest kwalifikator (forma 2).

**Praktyczny wniosek:** wysokie „opt" nie oznacza, że zdanie jest prawdziwe ani ważne —
można mieć `opt ≈ 0,33` przy `T1 = 0` (zdanie fałszywe), bo miary strukturalne i tak
wnoszą swoje. Dlatego ranking trzeba czytać przez T1 **łącznie z** T3, a nie przez samo
„opt". Każda miara niżej ma wprost zaznaczone: **za co odpowiada**, **jaki ma zakres**
i **kiedy bywa zerowa (i dlaczego)**.

---

## Wspólne oznaczenia

Podsumowanie lingwistyczne ma jedną z dwóch form:

- **Forma 1:** `Q P są S` (bez kwalifikatora) — np. *„Około połowy aut ma standardową długość"*.
- **Forma 2:** `Q P będących/mających W są S` (z kwalifikatorem `W`) — np. *„Około połowy
  ciężkich aut ma standardową długość"* (`W` = „masa = ciężki" zawęża populację).

Symbole:

- `m` — liczba krotek (rekordów) w bazie danych.
- `n` — liczba atrybutów w złożonym sumaryzatorze.
- `d_i` — i-ta krotka.
- `Q` — kwantyfikator lingwistyczny (np. *większość*, *około 1/4*), funkcja
  przynależności `μ_Q`.
- `S` — sumaryzator, możliwie złożony: `S_1, …, S_n` nad przestrzeniami `X_1, …, X_n`.
- `W` — kwalifikator (tylko forma 2), możliwie złożony: `W_{g_1}, …, W_{g_x}`.
- `μ_S(d_i)` — przynależność krotki `d_i` do sumaryzatora. Dla sumaryzatora złożonego:
  `μ_S(d_i) = min_j μ_{S_j}(V_j(y_i))` (t-norma po atrybutach).
- `in(A)` — **stopień rozmycia** zbioru rozmytego `A`:
  `in(A) = |supp(A)| / |X|` (miara nośnika podzielona przez miarę przestrzeni
  rozważań). Tu `|·|` to kardynalność zbioru skończonego lub całka z funkcji
  przynależności dla zbiorów ciągłych.

W blokach ze wzorami: `∑(i=1..m)` = suma, `∏(j=1..n)` = iloczyn, `ⁿ√(…)` = pierwiastek
n-tego stopnia (czyli średnia geometryczna), `∧` = t-norma (np. minimum).

---

## T1 — Stopień prawdziwości

**Za co odpowiada.** Najważniejsza miara: **czy zdanie jest prawdziwe**. Klasyczna miara
Yagera. Liczy, jaka część rekordów spełnia sumaryzator (średni stopień przynależności),
a potem sprawdza, czy ta proporcja „trafia" w kwantyfikator. Np. jeśli ~połowa aut ma
standardową długość, to zdanie *„około połowy aut ma standardową długość"* dostaje `T1 = 1`.

**Zakres i interpretacja.** `[0,1]`; `1` = zdanie w pełni prawdziwe, `0` = fałszywe.
To jedna z dwóch miar (obok T3) realnie zależnych od danych.

**Kiedy bywa zerowe / pułapki.**
- `T1 = 0`, gdy proporcja nie sięga nośnika kwantyfikatora — typowe dla **sumaryzatorów
  złożonych** (koniunkcja rzadko bywa spełniana). W projekcie *„Większość aut jest
  dynamiczna i paliwożerna"* ma `T1 = 0`, bo oba warunki naraz spełnia tylko ~13,9%
  rekordów, a „większość" wymaga ≥60%.
- **Pułapka:** wysokie `T1` samo w sobie nic nie znaczy. Zdanie *„prawie żadne auto nie
  ma [rzadkiej cechy]"* osiąga `T1 = 1` trywialnie (proporcja ≈ 0 idealnie trafia
  w „prawie żaden"). Dlatego T1 czytaj **razem z T3**.

**W formie 2** liczone jest *warunkowo* — tylko wśród rekordów spełniających kwalifikator
(„jaka część *ciężkich* aut ma daną cechę"). Dla kwantyfikatora **bezwzględnego**
argumentem `μ_Q` jest sama suma `∑ μ_S(dᵢ)` (liczba), nie proporcja.

**Wzór (referencyjnie).**

Forma 1 (`Q P są S`):

```
            1
T₁ = μ_Q( ─── · ∑(i=1..m) μ_S(dᵢ) )
            m
```

Forma 2 (`Q P będących W są S`):

```
              ∑(i=1..m) ( μ_S(dᵢ) ∧ μ_W(dᵢ) )
T₁ = μ_Q( ───────────────────────────────────────── )
                     ∑(i=1..m) μ_W(dᵢ)
```

---

## T2 — Stopień nieprecyzyjności (sumaryzatora)

**Za co odpowiada.** Czy użyte etykiety są **ostre, czy rozmyte**. „Standardowa długość"
o wąskim nośniku jest precyzyjna; etykieta rozlewająca się na pół skali — nieprecyzyjna.
Miara strukturalna: zależy od projektu zbiorów rozmytych, **nie** od danych.

**Zakres i interpretacja.** `[0,1]`; `1` = bardzo precyzyjny (wąskie zbiory rozmyte
względem uniwersum), niżej = bardziej „rozmazany". W projekcie zwykle `≈ 0,85–0,92`
(etykiety są wąskie względem pełnego zakresu atrybutu, więc sumaryzator jest „ostry").

**Kiedy bywa zerowe.** W praktyce nie schodzi do 0 — to wymagałoby etykiety pokrywającej
nośnikiem całe uniwersum. Im węższy nośnik, tym bliżej 1.

**Wzór (referencyjnie).** To 1 minus średnia geometryczna stopni rozmycia atomów:

```
T₂ = 1 - ⁿ√( ∏(j=1..n) in(Sⱼ) )           ⁿ√ = pierwiastek n-tego stopnia
                                              (czyli średnia geometryczna)
          |supp(Sⱼ)|
in(Sⱼ) = ────────────
            |Xⱼ|
```

`supp(Sⱼ)` — nośnik etykiety; `Xⱼ` — pełen zakres atrybutu.

---

## T3 — Stopień pokrycia

**Za co odpowiada.** **Jak dużą część rekordów** podsumowanie faktycznie obejmuje. Druga
(obok T1) miara realnie zależna od danych — i to ona zwykle decyduje o kolejności
w rankingu przy równym T1.

**Zakres i interpretacja.** `[0,1]`; `0,694` znaczy „dotyczy 69,4% rekordów". Wyższe =
zdanie mówi o większej części bazy. W projekcie *„długość = standardowy"* (`T3 = 0,694`)
bije rzadszą *„poj. skokowa = małolitrażowy"* (`T3 = 0,314`) mimo że obie mają `T1 = 1`.

**Kiedy bywa niskie / o czym to świadczy.** Niskie T3 **nie zawsze jest złe**:
*„Prawie żadne ciężkie auto nie ma małolitrażowego silnika"* ma `T3 = 0,067`, ale
`T1 = 1` — niskie pokrycie niesie tu realną, nieoczywistą informację. T3 to „o ilu
rekordach mówimy", nie „czy to prawda".

**Wzór (referencyjnie).**

Forma 2 (z kwalifikatorem): ułamek krotek spełniających `W`, które trafia też sumaryzator:

```
       ∑(i=1..m) tᵢ
T₃ = ───────────────
       ∑(i=1..m) hᵢ

tᵢ = 1, gdy μ_S(dᵢ) > 0 oraz μ_W(dᵢ) > 0;   inaczej tᵢ = 0
hᵢ = 1, gdy μ_W(dᵢ) > 0;                      inaczej hᵢ = 0
```

Forma 1 (bez kwalifikatora) — po prostu udział rekordów trafionych przez sumaryzator:

```
       |supp(S ∩ D)|     #{ i : μ_S(dᵢ) > 0 }
T₃ = ─────────────── = ───────────────────────
             m                     m
```

---

## T4 — Stopień trafności

**Za co odpowiada.** Czy współwystępowanie cech jest **zaskakujące/informatywne**, czy
tylko oczekiwane. Porównuje faktyczne pokrycie złożonego sumaryzatora z tym, czego
oczekiwalibyśmy, gdyby cechy były niezależne. To miara odróżniająca podsumowania
nietrywialne od oczywistych.

**Zakres i interpretacja.** `[0,1]`; wyżej = bardziej trafne (silniejsza korelacja
między atomami niż przy niezależności).

**Kiedy bywa zerowe — ważne.** **Dla sumaryzatora prostego (`n = 1`) `T4 ≡ 0`** — wzór
degeneruje się do `|r₁ − T₃| = 0`, bo jedyny atom *jest* całym sumaryzatorem. Dlatego
w całej formie I prostej widzisz `T4 = 0,000` w każdym wierszu. Miara **ożywa dopiero
dla sumaryzatorów złożonych** (AND/OR): w projekcie *„moc = dynamiczny ∧ spalanie =
paliwożerny"* daje `T4 = 0,037`, a łatwiejsza do spełnienia alternatywa *„V_max =
wyścigowy ∨ moc = dynamiczny"* — `T4 = 0,301`.

**Wzór (referencyjnie).**

```
T₄ = | ∏(j=1..n) rⱼ  −  T₃ |

        1
rⱼ = ─── · ∑(i=1..m) gᵢⱼ
        m

gᵢⱼ = 1, gdy μ_{Sⱼ}(Vⱼ(yᵢ)) > 0;   inaczej gᵢⱼ = 0
```

`rⱼ` — udział rekordów spełniających pojedynczy atom `j`; `∏ rⱼ` to „oczekiwane" pokrycie
przy założeniu niezależności atrybutów.

---

## T5 — Długość podsumowania

**Za co odpowiada.** **Czytelność**: krótsze zdanie (mniej etykiet w sumaryzatorze) jest
lepsze. Czysto formalna kara za złożoność — nie zależy ani od danych, ani od kształtu
zbiorów, tylko od liczby etykiet.

**Zakres i interpretacja.** `(0,1]`; `|S|=1 → T5 = 1` (najkrótsze, najczytelniejsze),
i maleje **wykładniczo o połowę** z każdą dodaną etykietą: 2 etykiety → `0,5`,
3 etykiety → `0,25`. W projekcie sumaryzator złożony z 3 cech dostaje za to wyraźną karę.

**Wzór (referencyjnie).** `|S|` — liczba zbiorów rozmytych w sumaryzatorze.

```
T₅ = 2 · (0,5)^|S|
```

---

## T6 — Stopień nieprecyzyjności kwantyfikatora

**Za co odpowiada.** To samo co T2, ale dla **kwantyfikatora `Q`** — jak ostry jest sam
kwantyfikator („dokładnie 30" jest precyzyjny, „większość" rozmyta). Strukturalna,
zależy od wyboru `Q`.

**Zakres i interpretacja.** `[0,1]`; `1` = precyzyjny kwantyfikator. W projekcie węższa
*„mniejszość"* (`T6 = 0,65`) jest bardziej specyficzna niż szersza *„około połowy"*
(`T6 = 0,60`) — i to różnicuje wiersze rankingu o tym samym T1 i T3.

**Wzór (referencyjnie).**

```
                       |supp(Q)|
T₆ = 1 - in(Q) = 1 - ────────────
                         |X_Q|
```

`|X_Q| = 1` dla kwantyfikatora **względnego** (przestrzeń `[0,1]`) lub `|X_Q| = m` dla
**bezwzględnego**.

---

## T7 — Stopień kardynalności kwantyfikatora

**Za co odpowiada.** Jak T6, ale używa **pola pod funkcją przynależności** `Q`, a nie
samego nośnika — łapie różnice kształtu nawet przy tym samym nośniku. Strukturalna.

**Zakres i interpretacja.** `[0,1]`; wyżej = kwantyfikator precyzyjniej opisuje ilość.
Zwykle idzie w parze z T6 (węższe „mniejszość" → wyższe T6 i T7).

**Wzór (referencyjnie).** `|Q|` — kardynalność / całka z funkcji przynależności `Q`;
`|X_Q|` jak w T6.

```
          |Q|
T₇ = 1 - ──────
          |X_Q|
```

---

## T8 — Stopień kardynalności sumaryzatora

**Za co odpowiada.** Jak T2 (nieprecyzyjność sumaryzatora), ale liczona na
**kardynalnościach** zbiorów `S_j` zamiast ich nośnikach — bierze pod uwagę kształt
funkcji przynależności, nie tylko jej szerokość. Strukturalna.

**Zakres i interpretacja.** `[0,1]`; wyżej = ostrzejszy sumaryzator. W projekcie
`≈ 0,88–0,95`.

**Wzór (referencyjnie).**

```
T₈ = 1 - ⁿ√( ∏(j=1..n) (|Sⱼ| / |Xⱼ|) )
```

---

## T9 — Stopień nieprecyzyjności kwalifikatora

**Za co odpowiada.** Odpowiednik T2/T6, ale dla **kwalifikatora `W`** (część „będących W").
Mówi, jak ostra jest etykieta zawężająca populację.

**Zakres i interpretacja.** `[0,1]`; wyżej = precyzyjniejszy kwalifikator. W projekcie
dla `W` = „masa = ciężki" wychodzi `T9 = 0,817`.

**Kiedy bywa zerowe — ważne.** **Istnieje tylko w formie 2.** W formie 1 (bez `W`)
`T9 = 0` z definicji — nie ma czego mierzyć. Te zera wchodzą do średniej „opt" i właśnie
dlatego forma 1 ma niższe „opt" niż forma 2 (różnica strukturalna, nie merytoryczna).

**Wzór (referencyjnie).**

Pojedynczy zbiór kwalifikatora:

```
T₉ = 1 - in(W)
```

Wiele zbiorów kwalifikatora `W_{g_1}, …, W_{g_x}`:

```
T₉ = 1 - ˣ√( ∏(j=1..x) in(W_gⱼ) )           ˣ√ = pierwiastek x-tego stopnia
```

---

## T10 — Stopień kardynalności kwalifikatora

**Za co odpowiada.** Odpowiednik T8/T7 dla **kwalifikatora `W`** — kardynalność (pole pod
funkcją przynależności) etykiety zawężającej. **Tylko forma 2** (w formie 1 `T10 = 0`).

**Zakres i interpretacja.** `[0,1]`; wyżej = precyzyjniejszy kwalifikator. W projekcie
`T10 = 0,875` dla „masa = ciężki".

**Wzór (referencyjnie).**

Pojedynczy zbiór kwalifikatora:

```
            |W|
T₁₀ = 1 - ──────
            |X_g|
```

Wiele zbiorów kwalifikatora:

```
T₁₀ = 1 - ˣ√( ∏(j=1..x) (|W_gⱼ| / |X_gⱼ|) )
```

---

## T11 — Długość kwalifikatora (miara dodatkowa, nie ponumerowana w książce)

**Za co odpowiada.** Symetryczny odpowiednik T5, ale liczy **długość kwalifikatora `W`** —
krótszy kwalifikator (mniej etykiet) daje czytelniejsze zdanie. Czysto formalna kara za
złożoność części „będących W".

**Zakres i interpretacja.** `(0,1]`; `|W|=1 → T11 = 1` i maleje o połowę z każdą dodaną
etykietą kwalifikatora. W projekcie kwalifikator jest jednoelementowy, więc `T11 = 1,000`.

**Kiedy bywa zerowe.** **Tylko forma 2** (wymaga `W`); w formie 1 `T11 = 0`.

**Wzór (referencyjnie).** `|W|` — liczba zbiorów rozmytych w kwalifikatorze.

```
T₁₁ = 2 · (0,5)^|W|
```

> Uwaga: ta miara nie występuje w książce źródłowej (kończy się na T10). To naturalny
> odpowiednik „długości kwalifikatora" względem T5. Jeśli polecenie zadania definiuje T11
> inaczej, należy odpowiednio podmienić wzór.

---

## TGM — Jakość podsumowania (podsumowanie optymalne)

**Za co odpowiada.** Jedna liczba agregująca wszystkie 11 miar do oceny „dobroci"
podsumowania (oznaczana **TGM**, w kodzie `optimal`). To **ważona średnia arytmetyczna**
miar (wzór 8.74 w książce). Podsumowanie optymalne to kandydat o najwyższym TGM.

**Zakres i interpretacja.** `[0,1]`; wyżej = lepsze. **Ale** — patrz „pułapka" niżej —
nie jest to miara prawdziwości i nie jest porównywalna między formami.

**Wzór (referencyjnie).**

```
T_GM = ∑(i=1..11) wᵢ · Tᵢ ,     przy czym   ∑(i=1..11) wᵢ = 1
```

```
S* =  arg max   ∑(i=1..11) wᵢ · Tᵢ
      S ∈ 𝕊
```

### Wagi w tym projekcie

Implementacja (`QualityWeights.equal()`) używa **wag równych dla wszystkich 11 miar**:
`w_i = 1/11`. Wagi są sterowalne z GUI, ale domyślnie każda miara waży tyle samo. Suma
w liczniku **zawsze biegnie po 11 miarach** — także po tych, które dla danej formy są
tożsamościowo równe 0.

Które miary są zerowe (i dlaczego), zależy od formy podsumowania:

- **Forma 1** (`Q P są S`, bez kwalifikatora): miary kwalifikatora **T9 = T10 = T11 = 0**
  (nie ma `W`, więc nie ma czego mierzyć). Dla **sumaryzatora prostego** dodatkowo
  **T4 = 0** (zob. sekcję T4). Aktywne pozostają **T1, T2, T3, T5, T6, T7, T8** —
  w szczególności **T3 (pokrycie) JEST liczone i niezerowe w formie 1**.
- **Forma 2** (`Q P będących W są S`): aktywne są **wszystkie 11 miar** (T9–T11
  niezerowe, bo istnieje kwalifikator).

**Skutek — wartości optymalne obu form NIE są porównywalne wprost.** W formie 1 trzy
zerowe miary kwalifikatora (T9–T11) wchodzą do średniej jako zera i ciągną ją w dół,
więc typowy `optimal` formy 1 to ~0,52, a formy 2 ~0,77. Ta różnica jest
**strukturalna** (ile zer trafia do średniej z 11), a nie merytoryczna — rankingi
porównuj **tylko wewnątrz jednej formy**.

> **Przykład (forma 1, miejsce 1 z programu):** `T1..T8 = 1,000; 0,860; 0,694; 0,000;
> 1,000; 0,600; 0,700; 0,910`, a `T9 = T10 = T11 = 0`. Stąd
> `optimal = (1,000 + 0,860 + 0,694 + 0 + 1,000 + 0,600 + 0,700 + 0,910 + 0 + 0 + 0) / 11
> = 0,524`. (Tu `T4 = 0`, bo sumaryzator jest prosty.)

> **Pułapka interpretacyjna:** wysokie „opt" ≠ prawda. W projekcie zdania *„Większość aut
> jest dynamiczna i paliwożerna"* ma `opt ≈ 0,33` przy `T1 = 0` (zdanie jest FAŁSZYWE) —
> te 0,33 pochodzi wyłącznie z niezerowych miar strukturalnych (T2, T6, T7, T8),
> niezależnych od danych. Zawsze czytaj „opt" przez pryzmat T1 i T3.

> **Wariant alternatywny (książkowy), NIEUŻYWANY w tym projekcie.** Niewiadomski zaleca,
> by przy braku innych przesłanek wagi były równe **tylko po miarach aktywnych** dla
> danej formy — miary nieaktywne dostają wagę 0, a resztę renormalizuje się do sumy 1.
> Wtedy forma 1 dzieliłaby przez liczbę aktywnych miar (a nie przez 11), `optimal` obu
> form byłby wprost porównywalny, a wartość formy 1 wyszłaby wyraźnie wyżej (rzędu 0,7
> zamiast 0,52). Ten projekt świadomie używa płaskiego `1/11` — trzeba o tym pamiętać,
> czytając liczby.

> **Uwaga o typie średniej:** książka (i ten projekt) używają średniej **arytmetycznej**.
> Wariant geometryczny `(∏_i T_i^{w_i})` jest ostrzejszy — dowolna miara bliska zeru
> ściąga cały wynik do ~0; tutaj go nie stosujemy.

---

## Zestawienie stosowalności

| Miara | Nazwa                      | Forma 1 | Forma 2 | Dotyczy          | Grupa |
|-------|----------------------------|:-------:|:-------:|------------------|-------|
| T1    | stopień prawdziwości       |    ✔    |    ✔    | całe podsumowanie| o danych |
| T2    | nieprecyzyjność            |    ✔    |    ✔    | sumaryzator `S`  | strukturalna |
| T3    | pokrycie                   |    ✔    |    ✔    | pokrycie         | o danych |
| T4    | trafność                   |  (0)\*  |    ✔    | sumaryzator `S`  | o formie |
| T5    | długość                    |    ✔    |    ✔    | sumaryzator `S`  | o formie |
| T6    | nieprecyzyjność kwant.     |    ✔    |    ✔    | kwantyfikator `Q`| strukturalna |
| T7    | kardynalność kwant.        |    ✔    |    ✔    | kwantyfikator `Q`| strukturalna |
| T8    | kardynalność sumaryzatora  |    ✔    |    ✔    | sumaryzator `S`  | strukturalna |
| T9    | nieprecyzyjność kwalif.    |    –    |    ✔    | kwalifikator `W` | o formie |
| T10   | kardynalność kwalif.       |    –    |    ✔    | kwalifikator `W` | o formie |
| T11   | długość kwalif. (dodana)   |    –    |    ✔    | kwalifikator `W` | o formie |

\* T4 jest aktywne w obu formach, ale **dla sumaryzatora prostego (`n=1`) wynosi 0** —
niezerowe staje się dopiero przy sumaryzatorach złożonych (AND/OR).

**Uwaga o „T11":** książka źródłowa kończy się na T10 — rozdziały 8, 9, 10 definiują
dokładnie T1–T10 (rozdziały 9 i 10 jedynie powtarzają je w wersjach przedziałowych i
typu 2). T11 („długość kwalifikatora") jest tu dodana przez analogię do T5, jako
brakująca symetryczna miara dla kwalifikatora. Dostosuj, jeśli polecenie zadania
określa ją inaczej.
