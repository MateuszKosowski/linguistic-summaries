# Multi-Subject Linguistic Summaries — Forms I–IV

Source: **A. Niewiadomski, I. Superson, "On multi-subjectivity in linguistic
summarization of relational databases", *Journal of Theoretical and Applied Computer
Science*, Vol. 8, No. 1, 2014, pp. 15–34** (Institute of Information Technology, Lodz
University of Technology).

This is the source for the assignment's *"podsumowania wielopodmiotowe w formach od I.
do IV."*. The book that defines the quality measures T1–T11 (Niewiadomski, *Methods for
the linguistic summarization of data*, 2008) only covers **single-subject** summaries
(two forms + T1–T11). Multi-subject summaries are a separate line of work.

The paper actually proposes **five** forms (Sec. 3.1–3.5); the assignment requires the
first four (eqs. (4), (14), (20), (24)). The paper also gives **type-2** variants
(eqs. (10), (17), (22), (26), (30)); this project uses **type-1** trapezoidal fuzzy
sets, so only the type-1 degree-of-truth formulas below apply. The fifth form (eq. (28),
generalization to groups of subjects) is out of scope here.

---

## Common notation

A multi-subject summary is linked to **two disjoint subjects** `P₁`, `P₂` — subsets of
the same table, described by the same attributes (e.g. two car makes in the `Make`
column, or "boys" vs "girls" by `Gender`). Splitting is done by a nominal attribute.

- `m` — number of tuples (records) in the database `D`.
- `dᵢ` — the i-th tuple; `dᵢ ∈* P₁` means `dᵢ` represents subject `P₁`.
- `M_{P₁} = Σᵢ t_{i,P₁}`, with `t_{i,P₁} = 1` if `dᵢ ∈* P₁`, else `0` — number of tuples
  of subject `P₁` (eqs. 7–9). `M_{P₂}` analogously.
- `Q` — **relative** linguistic quantifier (e.g. *most of*, *about half*), `μ_Q`.
- `S₁` — summarizer (fuzzy set, possibly compound), `μ_{S₁}`.
- `S₂` — qualifier (fuzzy set), present only in forms II and III, `μ_{S₂}`.
- **Σ-count restricted to a subject** (eq. 6):
  `Σcount(S₁_{P₁}) = Σ_{dᵢ ∈* P₁} μ_{S₁}(dᵢ)` — sigma-count of the summarizer over the
  tuples of `P₁` only.
- **Σ-count of an intersection** (eq. 16, t-norm = minimum):
  `Σcount((S₁∩S₂)_{P₁}) = Σ_{dᵢ ∈* P₁} min(μ_{S₁}(dᵢ), μ_{S₂}(dᵢ))`.
- `T ∈ [0,1]` — **degree of truth**; the only quality measure the paper defines for
  these forms (it notes that adapting other measures is possible but does not give them).

---

## Form I — `Q P₁ in comparison to P₂ are S₁`  (eq. 4)

Compares what *share* of each subject has property `S₁`, normalized by subject size.
Example: *"Most of boys in comparison to girls are tall"*.

$$
T = \mu_Q\!\left(
\frac{\frac{1}{M_{P_1}}\,\Sigma count(S_{1,P_1})}
     {\frac{1}{M_{P_1}}\,\Sigma count(S_{1,P_1}) + \frac{1}{M_{P_2}}\,\Sigma count(S_{1,P_2})}
\right)
$$

The argument of `μ_Q` is the relative "weight" of `P₁` (out of `P₁`+`P₂`) among objects
that are `S₁`, after correcting for the differing subject sizes. `Q` is relative.

---

## Form II — `Q P₁ in comparison to P₂ being S₂ are S₁`  (eq. 14)

Like Form I, but a qualifier `S₂` restricts **both** subjects (only tuples that are `S₂`
count). Example: *"About two-third of boys in comparison to girls being teenagers are
tall"*.

$$
T = \mu_Q\!\left(
\frac{\frac{1}{M_{P_1}}\,\Sigma count((S_1\cap S_2)_{P_1})}
     {\frac{1}{M_{P_1}}\,\Sigma count((S_1\cap S_2)_{P_1}) + \frac{1}{M_{P_2}}\,\Sigma count((S_1\cap S_2)_{P_2})}
\right)
$$

with `Σcount((S₁∩S₂)_{Pₖ}) = Σ_{dᵢ ∈* Pₖ} min(μ_{S₁}(dᵢ), μ_{S₂}(dᵢ))` (eq. 16).

---

## Form III — `Q P₁ being S₂ in comparison to P₂ are S₁`  (eq. 20)

The qualifier `S₂` restricts **only `P₁`**; `P₂` is taken without qualification. Example:
*"About half of boys being teenagers in comparison to girls are tall"*.

$$
T = \mu_Q\!\left(
\frac{\frac{1}{M_{P_1}}\,\Sigma count((S_1\cap S_2)_{P_1})}
     {\frac{1}{M_{P_1}}\,\Sigma count((S_1\cap S_2)_{P_1}) + \frac{1}{M_{P_2}}\,\Sigma count(S_{1,P_2})}
\right)
$$

Note the asymmetry: numerator and the `P₁` denominator term use the qualified count
`(S₁∩S₂)` on `P₁`, while the `P₂` term uses the plain `Σcount(S₁_{P₂})`.

---

## Form IV — `More P₁ than P₂ are S₁`  (eq. 24)

**No quantifier.** A direct, intuitive comparison of the raw sigma-counts. Example:
*"More boys than girls are tall"*.

$$
T = \frac{\Sigma count(S_{1,P_1})}{\Sigma count(S_{1,P_1}) + \Sigma count(S_{1,P_2})}
$$

`T > 0.5` means `P₁` has *more* of property `S₁` than `P₂` (in absolute sigma-count, not
normalized by subject size). This form uses no fuzzy quantifier model at all.

---

## Worked examples from the paper (used as test oracles)

- **Form I** (eq. 12–13): `Most of boys in comparison to girls are tall`. With
  `M_{P₁}=M_{P₂}=3`, `Σcount(S₁_{boys})=0.89`, `Σcount(S₁_{girls})=0.44`:
  argument `= (0.89/3) / (0.89/3 + 0.44/3) = 0.67`, then `μ_{most}(0.67) ≈ 0.56`.
- **Form IV** (eq. 27): `More boys than girls are tall` `= 0.89 / (0.89 + 0.44) ≈ 0.756`.

(Note: `0.89 = 0.22 + 0 + 0.67` and `0.44 = 0.44 + 0 + 0` come from the triangular
`tall` membership applied to the boys'/girls' heights in the paper's Table 4.)

---

## Applicability summary

| Form | Template | Quantifier `Q` | Qualifier `S₂` | Eq. |
|------|----------|:--------------:|:--------------:|:---:|
| I    | `Q P₁ in comparison to P₂ are S₁`            | relative | — | 4 |
| II   | `Q P₁ in comparison to P₂ being S₂ are S₁`   | relative | both subjects | 14 |
| III  | `Q P₁ being S₂ in comparison to P₂ are S₁`   | relative | only `P₁` | 20 |
| IV   | `More P₁ than P₂ are S₁`                     | none | — | 24 |

> Quality of multi-subject summaries: the paper defines only the **degree of truth `T`**
> for forms I–IV. Some single-subject measures depend solely on `S₁`/`Q` and could be
> reused, but they are not part of the source definition and are omitted here.
