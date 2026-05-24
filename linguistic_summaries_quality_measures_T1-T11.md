# Quality Measures of Linguistic Summaries (T1–T11)

Source: Niewiadomski, *Methods for the linguistic summarization of data — applications
of fuzzy sets and their extensions*, Chapter 8 (pp. 152–165).

The **book itself defines exactly 10 numbered quality measures, T1–T10**. The final
quality ("goodness of a summary") is a weighted average of those measures.

**T11 below is an additional measure ("length of a qualifier")** — it is NOT defined in
the book under that number. It is constructed here by direct analogy to T5 ("length of
a summary", Eq. 8.56), to fill the missing symmetric measure for the qualifier `W`:
the summarizer `S` has imprecision (T2), cardinality (T8) and length (T5); the qualifier
`W` has imprecision (T9) and cardinality (T10) but no length measure in the book.
T11 supplies it.

---

## Common notation

A linguistic summary has one of two forms:

- **Form 1:** `Q P are S` (no qualifier)
- **Form 2:** `Q P being/having W are S` (with qualifier `W`)

Symbols:

- `m` — number of tuples (records) in the database.
- `n` — number of attributes in a compound summarizer.
- `d_i` — the i-th tuple.
- `Q` — linguistic quantifier (e.g. *most*, *about 1/4*), membership function `μ_Q`.
- `S` — summarizer, possibly compound: `S_1, …, S_n` over universes `X_1, …, X_n`.
- `W` — qualifier (Form 2 only), possibly compound: `W_{g_1}, …, W_{g_x}`.
- `μ_S(d_i)` — membership of tuple `d_i` to the summarizer. For a compound summarizer:
  `μ_S(d_i) = min_j μ_{S_j}(V_j(y_i))` (t-norm over attributes).
- `in(A)` — **degree of fuzziness** of a fuzzy set `A`:
  `in(A) = |supp(A)| / |X|` (measure of the support divided by the measure of the
  universe). Here `|·|` is the cardinality of a finite set or the integral of the
  membership function for continuous sets.

---

## T1 — Degree of truth

Yager's classic measure: how true the statement is. Range `[0,1]`; closer to 1 is better.

**Form 1 (`Q P are S`):**

$$
T_1 = \mu_Q\!\left(\frac{1}{m}\sum_{i=1}^{m}\mu_S(d_i)\right)
$$

**Form 2 (`Q P being W are S`):**

$$
T_1 = \mu_Q\!\left(\frac{\sum_{i=1}^{m}\big(\mu_S(d_i)\wedge\mu_W(d_i)\big)}
{\sum_{i=1}^{m}\mu_W(d_i)}\right)
$$

- For an **absolute** quantifier, the argument of `μ_Q` is the bare sum
  `Σ_i μ_S(d_i)` (no division by `m`).
- `∧` is a t-norm (e.g. minimum).

---

## T2 — Degree of imprecision (of the summarizer)

How imprecise the summarizer is: wider supports → more imprecise → lower quality.
Range `[0,1]`; 1 = very precise (narrow fuzzy sets).

$$
T_2 = 1 - \left(\prod_{j=1}^{n} in(S_j)\right)^{1/n},
\qquad in(S_j) = \frac{|supp(S_j)|}{|X_j|}
$$

- `supp(S_j)` — support of fuzzy set `S_j`; `X_j` — universe (range) of attribute `j`.
- It is 1 minus the geometric mean of the degrees of fuzziness.

---

## T3 — Degree of covering

Fraction of the tuples matching the qualifier `W` that are covered by the summarizer.
Range `[0,1]`; 1 = the summarizer covers all relevant tuples.

**Form 2 (with qualifier):**

$$
T_3 = \frac{\sum_{i=1}^{m} t_i}{\sum_{i=1}^{m} h_i},
\qquad
t_i = \begin{cases} 1 & \mu_S(d_i) > 0 \text{ and } \mu_W(d_i) > 0 \\ 0 & \text{otherwise} \end{cases},
\qquad
h_i = \begin{cases} 1 & \mu_W(d_i) > 0 \\ 0 & \text{otherwise} \end{cases}
$$

**Form 1 (no qualifier)** reduces to:

$$
T_3 = \frac{|supp(S \cap \mathcal{D})|}{m}
    = \frac{\#\{\, i : \mu_S(d_i) > 0 \,\}}{m}
$$

---

## T4 — Degree of appropriateness

Whether the co-occurrence of features in the summarizer is "surprising"
(informative) or merely expected from the marginal distributions. This is the key
measure that distinguishes trivial summaries. Range `[0,1]`; higher = more
appropriate/informative.

$$
T_4 = \left|\,\prod_{j=1}^{n} r_j \;-\; T_3\,\right|,
\qquad
r_j = \frac{1}{m}\sum_{i=1}^{m} g_{ij},
\qquad
g_{ij} = \begin{cases} 1 & \mu_{S_j}(V_j(y_i)) > 0 \\ 0 & \text{otherwise} \end{cases}
$$

- `r_j` — share of tuples satisfying single attribute `j`; the product `Π_j r_j` is the
  "expected" covering under independence of attributes.

---

## T5 — Length of a summary

Shorter summaries are better (more readable). Range `(0,1]`; for `|S|=1` it equals 1
and decays exponentially with length.

$$
T_5 = 2 \cdot (0.5)^{|S|}
$$

- `|S|` — number of fuzzy sets composing the summarizer.

---

## T6 — Degree of quantifier imprecision

Analogue of T2 for the quantifier `Q` (shape of the support of `Q`). Range `[0,1]`;
1 = precise quantifier (e.g. *exactly 30*, *for all*).

$$
T_6 = 1 - in(Q) = 1 - \frac{|supp(Q)|}{|X_Q|}
$$

- `|X_Q| = 1` if `Q` is **relative** (universe `[0,1]`), or `|X_Q| = m` if `Q` is
  **absolute** (`m` = number of tuples).

---

## T7 — Degree of quantifier cardinality

Like T6, but uses the **cardinality of the fuzzy set `Q` itself** (area under the
membership function), not just its support — it captures shape differences even when
the support is the same. Range `[0,1]`; higher = quantifier describes the quantity
more precisely.

$$
T_7 = 1 - \frac{|Q|}{|X_Q|}
$$

- `|Q|` — cardinality / integral of the membership function of `Q`.
- `|X_Q|` as in T6 (1 for relative, `m` for absolute).

---

## T8 — Degree of summarizer cardinality

Like T2, but on the cardinalities of the sets `S_j` instead of their supports.
Range `[0,1]`.

$$
T_8 = 1 - \left(\prod_{j=1}^{n} \frac{|S_j|}{|X_j|}\right)^{1/n}
$$

---

## T9 — Degree of qualifier imprecision

**Form 2 only** (requires qualifier `W`). Range `[0,1]`.

Single qualifier set `W`:

$$
T_9 = 1 - in(W)
$$

Multiple qualifier sets `W_{g_1}, …, W_{g_x}`:

$$
T_9 = 1 - \left(\prod_{j=1}^{x} in(W_{g_j})\right)^{1/x}
$$

---

## T10 — Degree of qualifier cardinality

**Form 2 only.** Analogue of T9 on cardinalities. Range `[0,1]`.

Single qualifier set `W`:

$$
T_{10} = 1 - \frac{|W|}{|X_g|}
$$

Multiple qualifier sets:

$$
T_{10} = 1 - \left(\prod_{j=1}^{x} \frac{|W_{g_j}|}{|X_{g_j}|}\right)^{1/x}
$$

---

## T11 — Length of a qualifier (additional measure, not numbered in the book)

Symmetric counterpart of T5 for the qualifier `W`. Constructed by analogy to Eq. 8.56:
a shorter qualifier (fewer fuzzy sets) gives a more readable, higher-quality summary.
**Form 2 only** (requires a qualifier). Range `(0,1]`; for `|W|=1` it equals 1 and
decays exponentially with the number of fuzzy sets in the qualifier.

$$
T_{11} = 2 \cdot (0.5)^{|W|}
$$

- `|W|` — number of fuzzy sets composing the qualifier `W`. For a compound qualifier
  `W = W_{g_1}` AND/OR … AND/OR `W_{g_x}`, we have `|W| = x`.

> Note: this measure is not present in the source book (which stops at T10). It is the
> natural "length of qualifier" analogue of T5. If your assignment defines T11
> differently, replace this formula accordingly.

---

## TGM — Goodness of a summary (the main / overall measure)

The main quality measure of a summary, denoted **TGM**, is the **weighted arithmetic
mean** of the individual measures (Eq. 8.58 / 8.74 in the book). It aggregates
everything into a single score in `[0,1]`; higher = better summary.

$$
T_{GM} = \sum_{i} w_i \cdot T_i,
\qquad \sum_{i} w_i = 1
$$

Optimal summary = the one maximizing TGM over all candidate summaries `S`:

$$
S^{*} = \arg\max_{S \in \mathbb{S}}\ T_{GM}
       = \arg\max_{S \in \mathbb{S}} \sum_{i} w_i \cdot T_i
$$

Which measures enter the sum depends on the summary form (qualifier-related measures
are 0 for Form 1). Recommended simple equal weights:

- **Form 1** (no qualifier): use `T1, T2, T4, T5, T6, T7, T8` (7 measures),
  each with weight `1/7`; set `w_3 = w_9 = w_10 = w_11 = 0`
  (T3, T9, T10, T11 concern the qualifier).
- **Form 2** (with qualifier): use all `T1 … T11` (11 measures),
  each with weight `1/11`.

> Note: the book uses the **arithmetic** weighted mean for TGM. A geometric-mean
> variant would be `(∏_i T_i^{w_i})` — a stricter aggregation where any near-zero
> measure pulls the whole score to ~0. This assignment uses the arithmetic version.

---

## Applicability summary

| Measure | Name                      | Form 1 | Form 2 | Concerns        |
|---------|---------------------------|:------:|:------:|-----------------|
| T1      | degree of truth           |   ✔    |   ✔    | whole summary   |
| T2      | imprecision               |   ✔    |   ✔    | summarizer `S`  |
| T3      | covering                  |   ✔    |   ✔    | covering        |
| T4      | appropriateness           |   ✔    |   ✔    | summarizer `S`  |
| T5      | length                    |   ✔    |   ✔    | summarizer `S`  |
| T6      | quantifier imprecision    |   ✔    |   ✔    | quantifier `Q`  |
| T7      | quantifier cardinality    |   ✔    |   ✔    | quantifier `Q`  |
| T8      | summarizer cardinality    |   ✔    |   ✔    | summarizer `S`  |
| T9      | qualifier imprecision     |   –    |   ✔    | qualifier `W`   |
| T10     | qualifier cardinality     |   –    |   ✔    | qualifier `W`   |
| T11     | qualifier length (added)  |   –    |   ✔    | qualifier `W`   |

**Note on "T11":** the source book ends at T10 — chapters 8, 9, 10 all define exactly
T1–T10 (chapters 9 and 10 just restate them in interval-valued and type-2 versions).
T11 ("length of a qualifier") is added here by analogy to T5, as the missing symmetric
measure for the qualifier. Adjust if your assignment specifies it differently.
