package com.casinoplatform.ui.games.elevator

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.casinoplatform.ui.components.CasinoBetChip
import com.casinoplatform.ui.components.CasinoPrimaryButton
import com.casinoplatform.ui.components.CasinoSecondaryButton
import com.casinoplatform.ui.components.WalletBalanceBadge
import com.casinoplatform.ui.theme.DarkBackground
import com.casinoplatform.ui.theme.DarkSurface
import com.casinoplatform.ui.theme.DarkSurfaceBorder
import com.casinoplatform.ui.theme.DarkSurfaceElevated
import com.casinoplatform.ui.theme.NeonCyan
import com.casinoplatform.ui.theme.NeonEmerald
import com.casinoplatform.ui.theme.PrimaryGold
import com.casinoplatform.ui.theme.TextPrimary
import com.casinoplatform.ui.theme.TextSecondary
import kotlin.random.Random

@Composable
fun SkyElevatorScreen(
    userBalance: Long,
    onBalanceChange: (Long) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var betAmount by remember { mutableStateOf(100L) }
    var currentFloor by remember { mutableIntStateOf(0) } // 0 = Lobby, 1..6
    var isRiding by remember { mutableStateOf(false) }
    var isStalled by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf("Choose a door to ascend to Floor 1!") }

    val floorMultipliers = listOf(1.45, 2.15, 3.20, 5.00, 8.50, 18.00)

    val currentMultiplier = if (currentFloor > 0 && !isStalled) floorMultipliers[currentFloor - 1] else 1.0
    val currentPayout = (betAmount * currentMultiplier).toLong()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkSurfaceBorder)
                    .clickable(onClick = onBackClick)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("← LOBBY", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Text("SKY ELEVATOR", color = PrimaryGold, fontSize = 18.sp, fontWeight = FontWeight.Black)

            WalletBalanceBadge(balance = userBalance)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Floor Status HUD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (isStalled) Color(0xFF38151A)
                    else if (isRiding && currentFloor > 0) Color(0xFF261047)
                    else DarkSurface
                )
                .border(
                    1.dp,
                    if (isStalled) Color(0xFFFF3366)
                    else if (isRiding && currentFloor > 0) PrimaryGold
                    else DarkSurfaceBorder,
                    RoundedCornerShape(14.dp)
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isStalled) "ELEVATOR STALLED!"
                    else if (isRiding && currentFloor > 0) "FLOOR $currentFloor • ${currentMultiplier}x MULTIPLIER"
                    else "GROUND LOBBY (FLOOR 0)",
                    color = if (isStalled) Color(0xFFFF5252) else if (isRiding && currentFloor > 0) PrimaryGold else NeonCyan,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = statusText,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3 Elevator Doors for Current Floor Selection
        if (isRiding && !isStalled) {
            Text(
                text = "SELECT DOOR TO ASCEND TO FLOOR ${currentFloor + 1}",
                color = PrimaryGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf("DOOR A", "DOOR B", "DOOR C").forEachIndexed { doorIdx, doorName ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(90.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurfaceElevated)
                            .border(1.5.dp, PrimaryGold, RoundedCornerShape(12.dp))
                            .clickable {
                                // 1 out of 3 doors is a stall trap
                                val trapDoor = Random.nextInt(3)
                                if (doorIdx == trapDoor) {
                                    isStalled = true
                                    isRiding = false
                                    statusText = "Trap door selected! Elevator cable snapped."
                                } else {
                                    val nextFloor = currentFloor + 1
                                    currentFloor = nextFloor
                                    statusText = "Safe passage! Ascended to Floor $nextFloor (${floorMultipliers[nextFloor - 1]}x)!"
                                    if (nextFloor == 6) {
                                        val win = (betAmount * floorMultipliers[5]).toLong()
                                        onBalanceChange(userBalance + win)
                                        isRiding = false
                                        statusText = "PENTHOUSE REACHED! Won +$win VCOIN!"
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🚪", fontSize = 28.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(doorName, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Tower Floors Ladder
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF1B0A33), Color(0xFF110521), Color(0xFF090212))
                    )
                )
                .border(1.dp, Color(0xFF381566), RoundedCornerShape(16.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("TOWER FLOORS", color = PrimaryGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)

            floorMultipliers.reversed().forEachIndexed { revIdx, mult ->
                val floorNum = 6 - revIdx
                val isCurrent = currentFloor == floorNum
                val isPassed = currentFloor > floorNum

                val rowBg = when {
                    isCurrent && isStalled -> Color(0xFFFF3366)
                    isCurrent -> PrimaryGold
                    isPassed -> Color(0xFF7928CA)
                    else -> DarkSurfaceElevated.copy(alpha = 0.4f)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(rowBg)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (floorNum == 6) "PENTHOUSE (TOP)" else "FLOOR $floorNum",
                        color = if (isCurrent) DarkBackground else TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${mult}x",
                        color = if (isCurrent) DarkBackground else PrimaryGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bet Chips
        if (!isRiding) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(10L, 50L, 100L, 500L).forEach { amount ->
                    CasinoBetChip(
                        text = "$amount",
                        isSelected = betAmount == amount,
                        onClick = { betAmount = amount }
                    )
                }
                CasinoBetChip(
                    text = "MAX",
                    isSelected = betAmount == userBalance.coerceAtMost(3000L),
                    onClick = { betAmount = userBalance.coerceAtMost(3000L) }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Action Buttons
        if (!isRiding && !isStalled) {
            CasinoPrimaryButton(
                text = "ENTER ELEVATOR ($betAmount VCOIN)",
                enabled = userBalance >= betAmount,
                onClick = {
                    if (userBalance < betAmount) return@CasinoPrimaryButton
                    onBalanceChange(userBalance - betAmount)
                    isRiding = true
                    currentFloor = 0
                    isStalled = false
                    statusText = "Pick a door above to ascend to Floor 1!"
                }
            )
        } else if (isRiding && currentFloor > 0) {
            CasinoSecondaryButton(
                text = "EXIT ELEVATOR & BANK (+$currentPayout VCOIN)",
                onClick = {
                    onBalanceChange(userBalance + currentPayout)
                    isRiding = false
                    statusText = "Safely exited on Floor $currentFloor! Banked +$currentPayout VCOIN!"
                }
            )
        } else if (isStalled) {
            CasinoPrimaryButton(
                text = "TRY AGAIN",
                onClick = {
                    isStalled = false
                    isRiding = false
                    currentFloor = 0
                    statusText = "Place bet to enter elevator!"
                }
            )
        }
    }
}
