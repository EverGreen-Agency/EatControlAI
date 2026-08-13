#!/usr/bin/env bash
# Roda a vertical do rótulo através do DAT usando o Mock Device Kit oficial da Meta.
# Não precisa de óculos físicos — precisa de um celular Android conectado por USB.
#
# Uso:
#   ./scripts/dat-mock-test.sh

set -euo pipefail
cd "$(dirname "$0")/.."

if ! adb get-state >/dev/null 2>&1; then
  echo "Nenhum aparelho pronto. Conecte o celular por USB e autorize a depuração." >&2
  exit 1
fi

echo "→ aparelho:"
adb devices -l | tail -n +2 | sed '/^$/d'
echo

adb logcat -c
./gradlew :app:connectedDebugAndroidTest --console=plain \
  -Pandroid.testInstrumentationRunnerArguments.class=com.eatcontrolai.glasses.DatMockDeviceTest

echo
echo "→ log da sessão DAT:"
adb logcat -d -s EC_DAT:I -v raw
