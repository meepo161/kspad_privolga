package ru.avem.stand.tests

import ru.avem.stand.tests.business.*
import ru.avem.stand.view.composables.MotorType

object Lists {
    fun getTestsForDisplay(motor: MotorType) = listOf(
        InsulationResistanceMeasurement,
        InsulationResistanceMeasurementSensors,
        WindingInsulationTest,
        WindingInsulationTestSensors,
        DCWindingResistance,
        DCWindingResistanceSensors,
        Idling,
        ShortCircuit,
        TransformationRatio,
        TurnToTurnTest,
        Runout,
    ).filter { motor in it.availableMotors }
}
