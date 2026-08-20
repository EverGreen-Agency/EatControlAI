# Resumo legível do relatório JaCoCo por pacote.
# Uso: .\gradlew.bat :app:coverageReport ; powershell -File scripts\coverage-summary.ps1
param(
    [string]$ReportPath = "app/build/reports/jacoco/coverageReport/coverageReport.xml"
)

if (-not (Test-Path $ReportPath)) {
    Write-Error "Relatório não encontrado em $ReportPath. Rode primeiro: .\gradlew.bat :app:coverageReport"
    exit 1
}

[xml]$report = Get-Content $ReportPath

function Get-Ratio($counters, $type) {
    $counter = $counters | Where-Object { $_.type -eq $type }
    if (-not $counter) { return $null }
    $covered = [int]$counter.covered
    $missed = [int]$counter.missed
    $total = $covered + $missed
    if ($total -eq 0) { return $null }
    return [pscustomobject]@{
        Covered = $covered
        Total   = $total
        Percent = [math]::Round(100 * $covered / $total, 1)
    }
}

$rows = foreach ($package in $report.report.package) {
    $line = Get-Ratio $package.counter 'LINE'
    if (-not $line) { continue }
    $branch = Get-Ratio $package.counter 'BRANCH'
    [pscustomobject]@{
        Pacote     = $package.name
        Linhas     = "$($line.Percent)% ($($line.Covered)/$($line.Total))"
        Ramos      = if ($branch) { "$($branch.Percent)% ($($branch.Covered)/$($branch.Total))" } else { "sem ramos" }
        LinhasPct  = $line.Percent
    }
}

$rows | Sort-Object LinhasPct | Format-Table Pacote, Linhas, Ramos -AutoSize

$totalLine = Get-Ratio $report.report.counter 'LINE'
$totalBranch = Get-Ratio $report.report.counter 'BRANCH'
Write-Host ""
Write-Host ("TOTAL medido: linhas {0}% ({1}/{2}) | ramos {3}" -f `
    $totalLine.Percent, $totalLine.Covered, $totalLine.Total,
    $(if ($totalBranch) { "$($totalBranch.Percent)% ($($totalBranch.Covered)/$($totalBranch.Total))" } else { "sem ramos" }))
