# Wielopodmiotowe podsumowania lingwistyczne — formy I–IV

Źródło: **A. Niewiadomski, I. Superson, „On multi-subjectivity in linguistic
summarization of relational databases", *Journal of Theoretical and Applied Computer
Science*, Vol. 8, No. 1, 2014, s. 15–34** (Instytut Informatyki, Politechnika Łódzka).

To źródło dla wymaganych w zadaniu *„podsumowań wielopodmiotowych w formach od I.
do IV."*. Książka definiująca miary jakości T1–T11 (Niewiadomski, *Methods for the
linguistic summarization of data*, 2008) obejmuje tylko podsumowania **jednopodmiotowe**
(dwie formy + T1–T11). Podsumowania wielopodmiotowe to osobny nurt prac.

Artykuł proponuje w istocie **pięć** form (rozdz. 3.1–3.5); zadanie wymaga
pierwszych czterech (wzory (4), (14), (20), (24)). Artykuł podaje też warianty
**typu 2** (wzory (10), (17), (22), (26), (30)); ten projekt używa zbiorów rozmytych
trapezoidalnych **typu 1**, więc stosują się tylko poniższe wzory na stopień
prawdziwości dla typu 1. Piąta forma (wzór (28), uogólnienie na grupy podmiotów) jest
poza zakresem.

---

## Wspólne oznaczenia

Podsumowanie wielopodmiotowe wiąże się z **dwoma rozłącznymi podmiotami** `P₁`, `P₂` —
podzbiorami tej samej tabeli, opisywanymi tymi samymi atrybutami (np. dwie marki aut w
kolumnie `Make`, albo „chłopcy" vs „dziewczęta" wg `Gender`). Podział następuje wg
atrybutu nominalnego.

- `m` — liczba krotek (rekordów) w bazie `D`.
- `dᵢ` — i-ta krotka; `dᵢ ∈* P₁` oznacza, że `dᵢ` reprezentuje podmiot `P₁`.
- `M_{P₁} = Σᵢ t_{i,P₁}`, gdzie `t_{i,P₁} = 1`, gdy `dᵢ ∈* P₁`, w p.p. `0` — liczba
  krotek podmiotu `P₁` (wzory 7–9). `M_{P₂}` analogicznie.
- `Q` — **względny** kwantyfikator lingwistyczny (np. *większość*, *około połowy*), `μ_Q`.
- `S₁` — sumaryzator (zbiór rozmyty, możliwie złożony), `μ_{S₁}`.
- `S₂` — kwalifikator (zbiór rozmyty), obecny tylko w formach II i III, `μ_{S₂}`.
- **Σ-count ograniczony do podmiotu** (wzór 6):
  `Σcount(S₁_{P₁}) = Σ_{dᵢ ∈* P₁} μ_{S₁}(dᵢ)` — sigma-count sumaryzatora po krotkach
  tylko z `P₁`.
- **Σ-count przecięcia** (wzór 16, t-norma = minimum):
  `Σcount((S₁∩S₂)_{P₁}) = Σ_{dᵢ ∈* P₁} min(μ_{S₁}(dᵢ), μ_{S₂}(dᵢ))`.
- `T ∈ [0,1]` — **stopień prawdziwości**; jedyna miara jakości, jaką artykuł definiuje
  dla tych form (zaznacza, że adaptacja innych miar jest możliwa, lecz ich nie podaje).

---

## Forma I — `Q P₁ w porównaniu do P₂ są S₁`  (wzór 4)

Porównuje, jaki *udział* każdego podmiotu ma własność `S₁`, znormalizowany rozmiarem
podmiotu. Przykład: *„Większość chłopców w porównaniu do dziewcząt jest wysoka"*.

$$
T = \mu_Q\!\left(
\frac{\frac{1}{M_{P_1}}\,\Sigma count(S_{1,P_1})}
     {\frac{1}{M_{P_1}}\,\Sigma count(S_{1,P_1}) + \frac{1}{M_{P_2}}\,\Sigma count(S_{1,P_2})}
\right)
$$

Argumentem `μ_Q` jest względna „waga" `P₁` (z `P₁`+`P₂`) wśród obiektów będących `S₁`,
po korekcie na różne rozmiary podmiotów. `Q` jest względny.

---

## Forma II — `Q P₁ w porównaniu do P₂ będących S₂ są S₁`  (wzór 14)

Jak forma I, ale kwalifikator `S₂` zawęża **oba** podmioty (liczą się tylko krotki
będące `S₂`). Przykład: *„Około dwóch trzecich chłopców w porównaniu do dziewcząt,
będących nastolatkami, jest wysokich"*.

$$
T = \mu_Q\!\left(
\frac{\frac{1}{M_{P_1}}\,\Sigma count((S_1\cap S_2)_{P_1})}
     {\frac{1}{M_{P_1}}\,\Sigma count((S_1\cap S_2)_{P_1}) + \frac{1}{M_{P_2}}\,\Sigma count((S_1\cap S_2)_{P_2})}
\right)
$$

gdzie `Σcount((S₁∩S₂)_{Pₖ}) = Σ_{dᵢ ∈* Pₖ} min(μ_{S₁}(dᵢ), μ_{S₂}(dᵢ))` (wzór 16).

---

## Forma III — `Q P₁ będących S₂ w porównaniu do P₂ są S₁`  (wzór 20)

Kwalifikator `S₂` zawęża **tylko `P₁`**; `P₂` brane jest bez zawężenia. Przykład:
*„Około połowy chłopców będących nastolatkami w porównaniu do dziewcząt jest wysokich"*.

$$
T = \mu_Q\!\left(
\frac{\frac{1}{M_{P_1}}\,\Sigma count((S_1\cap S_2)_{P_1})}
     {\frac{1}{M_{P_1}}\,\Sigma count((S_1\cap S_2)_{P_1}) + \frac{1}{M_{P_2}}\,\Sigma count(S_{1,P_2})}
\right)
$$

Zwróć uwagę na asymetrię: licznik i człon mianownika dla `P₁` używają zawężonego
licznika `(S₁∩S₂)` na `P₁`, podczas gdy człon dla `P₂` używa zwykłego
`Σcount(S₁_{P₂})`.

---

## Forma IV — `Więcej P₁ niż P₂ jest S₁`  (wzór 24)

**Bez kwantyfikatora.** Bezpośrednie, intuicyjne porównanie surowych sigma-countów.
Przykład: *„Więcej chłopców niż dziewcząt jest wysokich"*.

$$
T = \frac{\Sigma count(S_{1,P_1})}{\Sigma count(S_{1,P_1}) + \Sigma count(S_{1,P_2})}
$$

`T > 0.5` oznacza, że `P₁` ma *więcej* własności `S₁` niż `P₂` (w bezwzględnym
sigma-count, bez normalizacji rozmiarem podmiotu). Ta forma w ogóle nie używa modelu
kwantyfikatora rozmytego.

---

## Przykłady policzone w artykule (jako wyrocznie testowe)

- **Forma I** (wzory 12–13): `Większość chłopców w porównaniu do dziewcząt jest wysoka`.
  Przy `M_{P₁}=M_{P₂}=3`, `Σcount(S₁_{chłopcy})=0.89`, `Σcount(S₁_{dziewczęta})=0.44`:
  argument `= (0.89/3) / (0.89/3 + 0.44/3) = 0.67`, następnie `μ_{większość}(0.67) ≈ 0.56`.
- **Forma IV** (wzór 27): `Więcej chłopców niż dziewcząt jest wysokich`
  `= 0.89 / (0.89 + 0.44) ≈ 0.756`.

(Uwaga: `0.89 = 0.22 + 0 + 0.67` oraz `0.44 = 0.44 + 0 + 0` pochodzą z trójkątnej
przynależności `wysoki` zastosowanej do wzrostów chłopców/dziewcząt z Tabeli 4 artykułu.)

---

## Zestawienie stosowalności

| Forma | Szablon | Kwantyfikator `Q` | Kwalifikator `S₂` | Wzór |
|-------|---------|:-----------------:|:-----------------:|:----:|
| I     | `Q P₁ w porównaniu do P₂ są S₁`            | względny | — | 4 |
| II    | `Q P₁ w porównaniu do P₂ będących S₂ są S₁`| względny | oba podmioty | 14 |
| III   | `Q P₁ będących S₂ w porównaniu do P₂ są S₁`| względny | tylko `P₁` | 20 |
| IV    | `Więcej P₁ niż P₂ jest S₁`                 | brak | — | 24 |

> Jakość podsumowań wielopodmiotowych: artykuł definiuje tylko **stopień prawdziwości
> `T`** dla form I–IV. Część miar jednopodmiotowych zależy wyłącznie od `S₁`/`Q` i dałoby
> się je ponownie wykorzystać, ale nie są one częścią definicji źródłowej i tutaj je
> pominięto.
