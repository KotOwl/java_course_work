import csv
import os

csv_path = "target/site/jacoco/jacoco.csv"
output_path = "doc/coverage_report.md"

if not os.path.exists(csv_path):
    print(f"Error: JaCoCo report not found at {csv_path}. Please run 'mvn clean test' first.")
    exit(1)

total_instructions_missed = 0
total_instructions_covered = 0
total_branches_missed = 0
total_branches_covered = 0

rows = []
with open(csv_path, newline='', encoding='utf-8') as csvfile:
    reader = csv.DictReader(csvfile)
    for row in reader:
        missed = int(row['INSTRUCTION_MISSED'])
        covered = int(row['INSTRUCTION_COVERED'])
        b_missed = int(row['BRANCH_MISSED'])
        b_covered = int(row['BRANCH_COVERED'])
        
        total_instructions_missed += missed
        total_instructions_covered += covered
        total_branches_missed += b_missed
        total_branches_covered += b_covered
        
        total_inst = missed + covered
        inst_cov = (covered / total_inst * 100) if total_inst > 0 else 100.0
        
        total_br = b_missed + b_covered
        br_cov = (b_covered / total_br * 100) if total_br > 0 else 100.0
        
        rows.append({
            'class': row['CLASS'],
            'package': row['PACKAGE'],
            'inst_cov': f"{inst_cov:.2f}% ({covered}/{total_inst})",
            'branch_cov': f"{br_cov:.2f}% ({b_covered}/{total_br})" if total_br > 0 else "N/A"
        })

total_inst_all = total_instructions_missed + total_instructions_covered
total_inst_cov_pct = (total_instructions_covered / total_inst_all * 100) if total_inst_all > 0 else 100.0

total_br_all = total_branches_missed + total_branches_covered
total_br_cov_pct = (total_branches_covered / total_br_all * 100) if total_br_all > 0 else 100.0

md_content = f"""# Звіт про покриття коду тестами (JaCoCo)

## Загальна статистика
- **Покриття інструкцій (Instructions):** {total_inst_cov_pct:.2f}% ({total_instructions_covered}/{total_inst_all})
- **Покриття розгалужень (Branches):** {total_br_cov_pct:.2f}% ({total_branches_covered}/{total_br_all})

## Детальний звіт по класах

| Клас | Пакет | Покриття інструкцій | Покриття розгалужень |
| :--- | :--- | :--- | :--- |
"""

for r in sorted(rows, key=lambda x: x['class']):
    md_content += f"| `{r['class']}` | `{r['package']}` | {r['inst_cov']} | {r['branch_cov']} |\n"

os.makedirs(os.path.dirname(output_path), exist_ok=True)
with open(output_path, "w", encoding="utf-8") as f:
    f.write(md_content)

print(f"Coverage report successfully written to {output_path}")
