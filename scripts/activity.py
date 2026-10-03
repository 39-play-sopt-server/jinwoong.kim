from collections import Counter
from datetime import datetime, timedelta, timezone
from pathlib import Path
import subprocess

ROOT = Path(__file__).resolve().parent.parent
KST = timezone(timedelta(hours=9))
today = datetime.now(KST).date()
start = today - timedelta(days=today.weekday() + 7 * 11)

result = subprocess.run(
    ["git", "log", "main", "--format=%ct"],
    cwd=ROOT, capture_output=True, text=True, check=True
)
counts = Counter(
    datetime.fromtimestamp(int(t), KST).date()
    for t in result.stdout.splitlines() if t.strip()
)

def color(n):
    if n == 0:
        return "#1e293b"
    return ["#c7d2fe", "#818cf8", "#6366f1", "#4338ca"][min(n, 4) - 1]

total = sum(n for day, n in counts.items() if start <= day <= today)
active = sum(1 for day in counts if start <= day <= today)
svg = [
    '<svg xmlns="http://www.w3.org/2000/svg" width="760" height="340" viewBox="0 0 760 340">',
    '<rect width="760" height="340" rx="20" fill="#0f172a"/>',
    '<g font-family="Arial,sans-serif">',
    '<text x="32" y="40" fill="#f8fafc" font-size="22" font-weight="bold">SERVER · ACTIVITY CALENDAR</text>',
    f'<text x="32" y="68" fill="#94a3b8" font-size="13">{start} — {today} · KST · main</text>',
    f'<text x="440" y="140" fill="#a5b4fc" font-size="36" font-weight="bold">{total}</text>',
    '<text x="440" y="164" fill="#94a3b8" font-size="14">COMMITS</text>',
    f'<text x="590" y="140" fill="#a5b4fc" font-size="36" font-weight="bold">{active}</text>',
    '<text x="590" y="164" fill="#94a3b8" font-size="14">ACTIVE DAYS</text>',
]
for row, label in enumerate(["M", "T", "W", "T", "F", "S", "S"]):
    svg.append(f'<text x="32" y="{119 + row * 24}" fill="#94a3b8" font-size="11">{label}</text>')

for i in range(84):
    day = start + timedelta(days=i)
    if day > today:
        continue
    x, y = 58 + (i // 7) * 28, 104 + (i % 7) * 24
    n = counts[day]
    svg.append(
        f'<rect x="{x}" y="{y}" width="20" height="18" rx="4" fill="{color(n)}">'
        f'<title>{day}: {n} commits</title></rect>'
    )

svg.append('<text x="32" y="310" fill="#94a3b8" font-size="12">LAST 12 WEEKS · Darker purple = more commits</text>')
svg.append('</g></svg>')
output = ROOT / "assets" / "activity.svg"
output.parent.mkdir(exist_ok=True)
output.write_text("\n".join(svg), encoding="utf-8")
print(f"달력 생성 완료: {output}")
