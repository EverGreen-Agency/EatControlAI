package com.eatcontrolai.ui.analyze

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.eatcontrolai.domain.menu.MenuAnalysis
import com.eatcontrolai.domain.plate.PlateAnalysis
import com.eatcontrolai.domain.plate.PlateFoodClass
import com.eatcontrolai.ui.components.EcChip
import com.eatcontrolai.ui.theme.EcColors
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MenuAnalysisPanel(
    analysis: MenuAnalysis,
    onSelect: (String) -> Unit,
    onRegister: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(EcColors.SurfaceRaised)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("OPÇÕES OBSERVADAS", style = MaterialTheme.typography.labelSmall, color = EcColors.TextMuted)
        analysis.options.forEach { option ->
            val selected = option.id == analysis.selectedOptionId
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (selected) EcColors.Blue.copy(alpha = 0.12f) else EcColors.Surface)
                    .clickable { onSelect(option.id) }
                    .padding(12.dp)
            ) {
                option.section?.let {
                    Text(it, style = MaterialTheme.typography.labelSmall, color = EcColors.TextFaint)
                }
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(option.name, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                    option.priceText?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall, color = EcColors.TextMuted)
                    }
                }
                if (option.description.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(option.description, style = MaterialTheme.typography.bodySmall, color = EcColors.TextMuted)
                }
                if (option.observedTerms.isNotEmpty()) {
                    Spacer(Modifier.height(7.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        option.observedTerms.take(3).forEach { term ->
                            EcChip(term, tone = EcColors.Amber, selected = true)
                        }
                    }
                }
            }
        }

        analysis.warnings.forEach { warning ->
            Text("• $warning", style = MaterialTheme.typography.bodySmall, color = EcColors.Amber)
        }

        Button(
            onClick = onRegister,
            enabled = analysis.selectedOption != null,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = EcColors.Mint, contentColor = EcColors.OnMint)
        ) {
            Text(if (analysis.registered) "Atualizar opção registrada" else "Confirmar e registrar opção")
        }
        Text(
            "O registro preserva o texto escolhido. Sem receita, porção e fonte de composição, calorias e macros ficam desconhecidos.",
            style = MaterialTheme.typography.bodySmall,
            color = EcColors.TextFaint
        )
    }
}

@Composable
fun PlateAnalysisPanel(
    analysis: PlateAnalysis,
    onToggle: (PlateFoodClass) -> Unit,
    onRegister: () -> Unit
) {
    val percent = NumberFormat.getPercentInstance(Locale.forLanguageTag("pt-BR")).apply {
        maximumFractionDigits = 0
    }
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(EcColors.SurfaceRaised)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("IDENTIFICAÇÃO ASSISTIDA", style = MaterialTheme.typography.labelSmall, color = EcColors.TextMuted)
        Text(
            "Provider: ${analysis.providerId} · ${analysis.providerVersion}",
            style = MaterialTheme.typography.bodySmall,
            color = EcColors.TextFaint
        )

        if (analysis.candidates.isNotEmpty()) {
            Text("Palpites do modelo", style = MaterialTheme.typography.titleSmall)
            analysis.candidates.forEach { candidate ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        candidate.foodClass?.displayName ?: "${candidate.rawLabel} · fora do recorte",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (candidate.foodClass == null) EcColors.TextFaint else EcColors.TextMuted
                    )
                    Text(percent.format(candidate.confidence), style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        analysis.warnings.forEach { warning ->
            Text("• $warning", style = MaterialTheme.typography.bodySmall, color = EcColors.Amber)
        }

        Text("Confirme ou corrija os componentes", style = MaterialTheme.typography.titleSmall)
        PlateFoodClass.entries.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEach { component ->
                    EcChip(
                        label = component.displayName,
                        tone = EcColors.BlueSoft,
                        selected = component in analysis.selectedComponents,
                        onClick = { onToggle(component) },
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }

        Button(
            onClick = onRegister,
            enabled = analysis.selectedComponents.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = EcColors.Mint, contentColor = EcColors.OnMint)
        ) {
            Text(if (analysis.registered) "Atualizar prato registrado" else "Confirmar e registrar componentes")
        }
        Text(
            "Esta versão não estima volume. Nenhum macro é calculado sem item, quantidade e fonte de composição auditáveis.",
            style = MaterialTheme.typography.bodySmall,
            color = EcColors.TextFaint
        )
    }
}
