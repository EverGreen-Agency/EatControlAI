#!/usr/bin/env bash
# Benchmark de modelos no aparelho físico.
#
# Requisitos: celular conectado por USB, depuração USB ativa, `adb devices` mostrando o aparelho.
# O que roda: app/src/androidTest/.../OcrBenchmarkTest.kt, que executa o mesmo harness da tela
# de Laboratório (com/eatcontrolai/benchmark/ProviderBenchmark.kt).
#
# Uso:
#   ./scripts/benchmark.sh

set -euo pipefail
cd "$(dirname "$0")/.."

echo "→ aparelhos conectados:"
adb devices -l | tail -n +2 | sed '/^$/d' || true
echo

if ! adb get-state >/dev/null 2>&1; then
  echo "Nenhum aparelho pronto. Conecte o celular por USB, autorize a depuração e tente de novo." >&2
  exit 1
fi

echo "→ limpando logcat"
adb logcat -c

echo "→ instalando e rodando o benchmark (pode levar alguns minutos na primeira vez)"
./gradlew :app:connectedDebugAndroidTest --console=plain

echo
echo "→ resultado:"
echo
adb logcat -d -s EC_BENCH:I -v raw
