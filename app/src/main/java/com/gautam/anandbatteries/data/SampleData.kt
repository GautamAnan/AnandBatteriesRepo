package com.gautam.anandbatteries.data

// Sample Exide battery data
object SampleData {
    fun getSampleBatteries(): List<Battery> = listOf(
        // Car Batteries
        Battery(
            id = "exide_fmi0_mreddin45rh",
            name = "Exide MREDDIN45RH",
            model = "FMI0-MREDDIN45RH",
            price = 5450.0,
            voltage = "12V",
            capacity = "45Ah",
            warranty = "48 Months",
            description = "Premium car battery with excellent starting power. Ideal for sedans and hatchbacks.",
            imageUrl = "",
            category = "Car Battery"
        ),
        Battery(
            id = "exide_fmi0_mreddin55d23lbh",
            name = "Exide MREDDIN55D23LBH",
            model = "FMI0-MREDDIN55D23LBH",
            price = 7200.0,
            voltage = "12V",
            capacity = "55Ah",
            warranty = "48 Months",
            description = "High-performance battery for modern cars with advanced features.",
            imageUrl = "",
            category = "Car Battery"
        ),
        Battery(
            id = "exide_fmi0_mreddin65d26rbh",
            name = "Exide MREDDIN65D26RBH",
            model = "FMI0-MREDDIN65D26RBH",
            price = 8500.0,
            voltage = "12V",
            capacity = "65Ah",
            warranty = "48 Months",
            description = "Heavy-duty battery for SUVs and premium cars with high electrical load.",
            imageUrl = "",
            category = "Car Battery"
        ),
        Battery(
            id = "exide_instabrite_ib500",
            name = "Exide Instabrite IB500",
            model = "IB500",
            price = 4850.0,
            voltage = "12V",
            capacity = "50Ah",
            warranty = "36 Months",
            description = "Reliable maintenance-free battery with consistent performance.",
            imageUrl = "",
            category = "Car Battery"
        ),
        Battery(
            id = "exide_epiq_ep65d23l",
            name = "Exide EPIQ EP65D23L",
            model = "EP65D23L",
            price = 9200.0,
            voltage = "12V",
            capacity = "65Ah",
            warranty = "48 Months",
            description = "Premium maintenance-free battery with advanced technology for luxury cars.",
            imageUrl = "",
            category = "Car Battery"
        ),

        // Two Wheeler Batteries
        Battery(
            id = "exide_xltz4",
            name = "Exide XLTZ4",
            model = "XLTZ4",
            price = 980.0,
            voltage = "12V",
            capacity = "3Ah",
            warranty = "36 Months",
            description = "Compact battery for bikes and scooters with reliable performance.",
            imageUrl = "",
            category = "Two Wheeler"
        ),
        Battery(
            id = "exide_xltz7",
            name = "Exide XLTZ7",
            model = "XLTZ7",
            price = 1450.0,
            voltage = "12V",
            capacity = "6Ah",
            warranty = "36 Months",
            description = "High-performance battery for motorcycles with advanced features.",
            imageUrl = "",
            category = "Two Wheeler"
        ),
        Battery(
            id = "exide_xplore_xltz9",
            name = "Exide Xplore XLTZ9",
            model = "XLTZ9",
            price = 1850.0,
            voltage = "12V",
            capacity = "8Ah",
            warranty = "48 Months",
            description = "Premium motorcycle battery with extended warranty and superior cranking power.",
            imageUrl = "",
            category = "Two Wheeler"
        ),

        // Inverter Batteries
        Battery(
            id = "exide_inva_master_imst1500",
            name = "Exide Inva Master IMST1500",
            model = "IMST1500",
            price = 12500.0,
            voltage = "12V",
            capacity = "150Ah",
            warranty = "36 Months",
            description = "Tall tubular battery for home inverters with long backup time.",
            imageUrl = "",
            category = "Inverter"
        ),
        Battery(
            id = "exide_inva_tubular_it500",
            name = "Exide Inva Tubular IT500",
            model = "IT500",
            price = 15800.0,
            voltage = "12V",
            capacity = "150Ah",
            warranty = "48 Months",
            description = "Premium tubular battery with excellent deep discharge recovery.",
            imageUrl = "",
            category = "Inverter"
        ),
        Battery(
            id = "exide_invamaster_imtt1800",
            name = "Exide InvaMaster IMTT1800",
            model = "IMTT1800",
            price = 18200.0,
            voltage = "12V",
            capacity = "180Ah",
            warranty = "60 Months",
            description = "High-capacity inverter battery for extended backup requirements.",
            imageUrl = "",
            category = "Inverter"
        ),
        Battery(
            id = "exide_inva_red_ir500",
            name = "Exide Inva Red IR500",
            model = "IR500",
            price = 14200.0,
            voltage = "12V",
            capacity = "150Ah",
            warranty = "42 Months",
            description = "Durable inverter battery with excellent charge acceptance.",
            imageUrl = "",
            category = "Inverter"
        ),

        // Commercial Vehicle Batteries
        Battery(
            id = "exide_poweron_po1450",
            name = "Exide PowerOn PO1450",
            model = "PO1450",
            price = 11500.0,
            voltage = "12V",
            capacity = "145Ah",
            warranty = "24 Months",
            description = "Heavy-duty battery for commercial vehicles and trucks.",
            imageUrl = "",
            category = "Commercial"
        ),
        Battery(
            id = "exide_cargo_master_cm1800",
            name = "Exide Cargo Master CM1800",
            model = "CM1800",
            price = 15800.0,
            voltage = "12V",
            capacity = "180Ah",
            warranty = "30 Months",
            description = "Extra heavy-duty battery for large commercial vehicles and buses.",
            imageUrl = "",
            category = "Commercial"
        )
    )
}

