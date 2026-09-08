# Estado Atual & Roadmap — EatControl

> **Status operacional, técnico e roadmap de entregas do produto.**
> Última sincronização: 07 de setembro de 2026.

---

## 1. Métricas de Engenharia e Saúde do Código

- **Suíte de Testes**: **172 testes unitários** (22 suítes), **0 falhas**, 100% de sucesso.
- **DSR (Decision Success Rate)**: 100% nos 19 cenários clínicos canônicos.
- **Cobertura do Domínio Determinístico**: > 99% de cobertura em linhas críticas.
- **Tamanho do APK de Release**: ~44,1 MB (arm64-v8a) com R8 habilitado.
- **Arquitetura**: Separação estrita de camadas (`core/model`, `domain/`, `inference/`, `glasses/`, `orchestration/`, `ui/`).

---

## 2. O Que Está Pronto (Shipado)

| Componente | Status | Detalhes |
| :--- | :---: | :--- |
| **Trilhas de Análise** | ✅ Pronto | 4 trilhas operacionais: Barcode (EAN-13), Rótulo (OCR), Cardápio (OCR + parser) e Prato assistido. |
| **Cascata Automática** | ✅ Pronto | Modo automático roteia pela cascata de menor custo computacional/bateria. |
| **Motor Determinístico** | ✅ Pronto | `FoodDecisionEngine` com precedência de 5 ranks de evidência e 4 estados explícitos. |
| **Base de Produtos Real** | ✅ Pronto | Consulta à API pública do **Open Food Facts** integrada após o catálogo local. |
| **Camada GLP-1** | ✅ Pronto | `glp1-rules-v1` conectada ao orquestrador, à interface e aos comandos de voz. |
| **Regras & Sintomas** | ✅ Pronto | Cadastro de regras pessoais e registro de sintomas/desconfortos com disclaimers clínicos. |
| **Voz On-Device** | ✅ Pronto | STT nativo sem rede e TTS pt-BR com medição de *time-to-first-audio*. |
| **BrandKit v1.0** | ✅ Pronto | Logos, paleta de cores *Precision Intelligence*, tokens CSS e tipografia configurados. |
| **Web & Vercel** | ✅ Pronto | Landing page em [web/](file:///c:/Users/Lenovo/AndroidStudioProjects/EatControlAI/web) com termos, política de privacidade, sitemap e roadmap público. |
| **Deck Slush 100** | ✅ Pronto | 13 slides em HTML, PDF e PPTX (4 variantes geradas, sem placeholders). |

---

## 3. O Que Está Em Construção (Próxima Fronteira)

1. **Publicação na Google Play Store**:
   - Geração e custódia segura da keystore de release (`.jks`).
   - Configuração de build flavors: `phone` (versão pública leve sem dependências desnecessárias) e `glasses` (canal beta wearable).
   - Gerar artefato AAB (*Android App Bundle*).
   - Iniciar o teste fechado obrigatório do Google Play (12 testadores durante 14 dias).
2. **Validação Clínica Formal (Ponto de Auditoria EQ-11)**:
   - Coleta de assinatura e registro formal (CRM/CRN) do médico/nutricionista parceiro para respaldar juridicamente a citação de validação clínica.
3. **Monetização e Paywall**:
   - Definição da fronteira freemium (decisão pontual gratuita vs histórico prolongado e regras avançadas na assinatura via Google Play Billing).
4. **Visão de Prato Aprimorada**:
   - Evolução da classificação de pratos mantendo a declaração explícita de incerteza (nunca inventar calorias sem peso confirmado).

---

## 4. O Que Decidimos NÃO Fazer (Por Enquanto)

1. **Estimar peso/calorias por foto**: Os apps convencionais erram grosseiramente ao inferir gramatura visualmente. O EatControl pergunta a porção ou usa a porção declarada.
2. **Sincronização obrigatória de dados de saúde na nuvem**: O histórico do paciente é *local-first* no aparelho (via DataStore criptografado) para proteger a privacidade sob a LGPD.
3. **Exigência de óculos para usar o app**: Os óculos inteligentes são um acelerador de conveniência, mas o app mobile de smartphone é 100% autônomo.
