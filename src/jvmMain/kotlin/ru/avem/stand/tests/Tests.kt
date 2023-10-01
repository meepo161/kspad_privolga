package ru.avem.stand.tests

import ru.avem.stand.tests.business.*
import ru.avem.stand.view.composables.MotorType

object Tests {
    fun getTestsForDisplay(motor: MotorType) = getAllTests().filter { motor in it.availableMotors }

    fun getAllTests() = listOf(
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
    )

    fun getTestNameByTag(tag: String) =
        getAllTests().first {
            it.tag == tag
        }.name

}
