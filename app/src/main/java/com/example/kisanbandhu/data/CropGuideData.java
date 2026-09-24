package com.example.kisanbandhu.data;

import com.example.kisanbandhu.models.FertilizerAdvice;
import com.example.kisanbandhu.models.PestInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Static agronomy knowledge per crop, kept as plain Java arrays so it is
 * easy to read and extend. Doses are INDICATIVE (per acre) - always follow
 * local Krishi Vigyan Kendra (KVK) / product label advice.
 *
 * Each crop has 5 growth stages (see CropStageHelper):
 *   0 Sowing/Establishment, 1 Vegetative, 2 Flowering,
 *   3 Grain/Fruit development, 4 Harvest.
 */
public class CropGuideData {

    private static final Map<String, String[][]> STAGES = new HashMap<>();
    private static final Map<String, String[][]> FERTILIZERS = new HashMap<>();
    private static final Map<String, String[][]> PESTS = new HashMap<>();

    static {
        // ---------- STAGES: {stage name, main activity} x5 ----------
        STAGES.put("Rice", new String[][]{
                {"Nursery & Transplanting", "Prepare nursery, puddle the field and transplant 20-25 day old seedlings."},
                {"Tillering", "Keep 2-5 cm standing water; first weeding around day 20."},
                {"Panicle Initiation & Flowering", "Do not let the field dry; watch for stem borer and blast."},
                {"Grain Filling", "Maintain moisture; guard against birds and leaf folder."},
                {"Harvest", "Drain field 10 days before; harvest when about 80% grains turn golden."}});
        STAGES.put("Wheat", new String[][]{
                {"Sowing & Germination", "Sow in rows with treated seed; give a light first irrigation."},
                {"Crown Root & Tillering", "Irrigate at about 21 days (CRI stage); weed control."},
                {"Jointing & Flowering", "Irrigate at booting/flowering; watch for rust."},
                {"Grain Filling", "Light irrigation at milking stage; avoid lodging."},
                {"Harvest", "Harvest when grains are hard and straw turns golden yellow."}});
        STAGES.put("Maize", new String[][]{
                {"Sowing & Emergence", "Sow on ridges; thin plants to correct spacing."},
                {"Vegetative (Knee-high)", "Weeding and earthing up; watch for fall armyworm."},
                {"Tasseling & Silking", "Most water-sensitive stage - do not allow moisture stress."},
                {"Cob Filling", "Irrigate at grain filling; monitor for cob borers."},
                {"Harvest", "Harvest when husks dry and kernels are hard."}});
        STAGES.put("Groundnut", new String[][]{
                {"Sowing & Germination", "Treat seed with fungicide/Rhizobium; sow on well-drained soil."},
                {"Vegetative", "Weeding at 20-25 days; watch for leaf miner."},
                {"Flowering & Pegging", "Earthing up and gypsum application; keep soil moist."},
                {"Pod Development", "Avoid waterlogging; monitor for tikka leaf spot."},
                {"Harvest", "Harvest when leaves yellow and pods show dark inner veins."}});
        STAGES.put("Cotton", new String[][]{
                {"Sowing & Establishment", "Sow on ridges, gap-fill after 10 days."},
                {"Vegetative", "Thinning, weeding and first sucking-pest scouting."},
                {"Squaring & Flowering", "Irrigate at flowering; scout for bollworms and whitefly."},
                {"Boll Development", "Keep moisture steady; install pheromone traps."},
                {"Picking", "Pick fully opened bolls in dry weather, in 2-3 rounds."}});
        STAGES.put("Soybean", new String[][]{
                {"Sowing & Germination", "Seed treatment with Rhizobium; sow after good rain."},
                {"Vegetative", "Weeding at 20 and 40 days; monitor girdle beetle."},
                {"Flowering & Pod Setting", "Irrigate if dry spell; watch for yellow mosaic virus."},
                {"Pod Filling", "Scout for pod borers and leaf-eating caterpillars."},
                {"Harvest", "Harvest when leaves drop and pods turn brown."}});
        STAGES.put("Sugarcane", new String[][]{
                {"Planting & Germination", "Plant treated 2-3 bud setts in furrows; light irrigation."},
                {"Tillering", "Weeding and earthing up; watch for early shoot borer."},
                {"Grand Growth", "Highest water need; tie/trash-mulch to prevent lodging."},
                {"Maturity & Ripening", "Reduce irrigation to raise sugar content."},
                {"Harvest", "Harvest at ground level when Brix is high and leaves dry."}});
        STAGES.put("Bajra (Pearl Millet)", new String[][]{
                {"Sowing & Emergence", "Sow with first monsoon rains; thin at 10-15 days."},
                {"Vegetative", "Weeding; check for downy mildew and shoot fly."},
                {"Panicle & Flowering", "Protect from moisture stress at flowering."},
                {"Grain Filling", "Scare birds; watch for ergot."},
                {"Harvest", "Harvest when grains are hard and earheads dry."}});
        STAGES.put("Chickpea (Gram)", new String[][]{
                {"Sowing & Germination", "Seed treatment for wilt; sow on conserved moisture."},
                {"Vegetative", "One weeding at 25-30 days; pinch tips for branching."},
                {"Flowering", "Avoid irrigation during peak flowering; watch pod borer."},
                {"Pod Filling", "Install pheromone traps; one light irrigation if dry."},
                {"Harvest", "Harvest when leaves turn yellow-brown and pods rattle."}});
        STAGES.put("Mustard", new String[][]{
                {"Sowing & Germination", "Sow in lines, thin to 10-15 cm plant spacing."},
                {"Rosette / Vegetative", "First irrigation at 25-30 days; weeding."},
                {"Flowering", "Irrigate at flowering; aphids peak here."},
                {"Pod Filling", "Second irrigation at pod formation; monitor white rust."},
                {"Harvest", "Harvest when 75% pods turn yellow-brown."}});
        STAGES.put("Watermelon", new String[][]{
                {"Sowing & Germination", "Sow on raised beds with drip or basin irrigation."},
                {"Vine Growth", "Weeding, mulching; train vines."},
                {"Flowering & Fruit Set", "Keep moisture even; bees are needed for pollination."},
                {"Fruit Development", "Reduce watering slightly near ripening; watch for fruit fly."},
                {"Harvest", "Harvest when tendril near fruit dries and ground spot turns yellow."}});

        // ---------- FERTILIZERS: 3 entries {chemical, organic}: basal, vegetative, flowering ----------
        FERTILIZERS.put("Rice", new String[][]{
                {"DAP 50 kg + MOP 20 kg per acre at transplanting.", "FYM 4 tonne per acre + green manure/Azolla."},
                {"Urea 30 kg per acre in two splits (tillering).", "Vermicompost 500 kg per acre + Azospirillum."},
                {"Urea 15 kg per acre at panicle initiation.", "Panchagavya 3% foliar spray."}});
        FERTILIZERS.put("Wheat", new String[][]{
                {"DAP 50 kg + MOP 20 kg per acre at sowing.", "FYM 4 tonne per acre + Azotobacter seed treatment."},
                {"Urea 35 kg per acre at first irrigation (~21 days).", "Vermicompost 500 kg per acre."},
                {"Urea 20 kg per acre at booting only if leaves are pale.", "Jeevamrut drench / compost tea."}});
        FERTILIZERS.put("Maize", new String[][]{
                {"DAP 50 kg + MOP 20 kg + Zinc sulphate 10 kg per acre.", "FYM 4 tonne per acre + Azotobacter."},
                {"Urea 35 kg per acre at knee-high stage.", "Vermicompost 400 kg + Jeevamrut."},
                {"Urea 20 kg per acre at tasseling.", "Neem cake 100 kg + compost side dressing."}});
        FERTILIZERS.put("Groundnut", new String[][]{
                {"SSP 100 kg + DAP 20 kg per acre at sowing.", "FYM 3 tonne per acre + Rhizobium/PSB seed treatment."},
                {"MOP 10 kg per acre only if soil potassium is low.", "Compost tea / Jeevamrut."},
                {"Gypsum 200 kg per acre at pegging (calcium for pods).", "Bone meal or FYM 1 tonne per acre."}});
        FERTILIZERS.put("Cotton", new String[][]{
                {"DAP 50 kg + MOP 20 kg per acre at sowing.", "FYM 4 tonne per acre + Azotobacter."},
                {"Urea 30 kg per acre at squaring.", "Vermicompost 500 kg per acre."},
                {"Urea 30 kg + MOP 15 kg per acre at flowering/boll setting.", "Neem cake 150 kg + Panchagavya spray."}});
        FERTILIZERS.put("Soybean", new String[][]{
                {"SSP 100 kg + MOP 15 kg per acre (little nitrogen needed).", "FYM 3 tonne + Rhizobium + PSB seed treatment."},
                {"Usually no nitrogen; foliar 2% DAP if crop is pale.", "Jeevamrut drench."},
                {"Sulphur/Zinc foliar spray if deficiency is seen.", "Compost tea foliar spray."}});
        FERTILIZERS.put("Sugarcane", new String[][]{
                {"DAP 100 kg + MOP 40 kg per acre at planting.", "FYM 8 tonne per acre + press mud."},
                {"Urea 60 kg per acre at tillering (in 2 splits).", "Vermicompost 1 tonne + Azospirillum."},
                {"Urea 60 kg per acre at grand growth, then stop N.", "Neem cake 200 kg + trash mulching."}});
        FERTILIZERS.put("Bajra (Pearl Millet)", new String[][]{
                {"DAP 25 kg per acre at sowing.", "FYM 3 tonne per acre + Azospirillum."},
                {"Urea 20 kg per acre at 3-4 weeks.", "Vermicompost 300 kg per acre."},
                {"Urea 10 kg per acre only if rainfall is good.", "Jeevamrut drench."}});
        FERTILIZERS.put("Chickpea (Gram)", new String[][]{
                {"DAP 40 kg per acre at sowing (starter dose).", "FYM 3 tonne + Rhizobium + PSB seed treatment."},
                {"No nitrogen; 2% urea foliar spray only if very pale.", "Jeevamrut drench."},
                {"Sulphur 8 kg per acre if soil is deficient.", "Compost tea foliar spray."}});
        FERTILIZERS.put("Mustard", new String[][]{
                {"DAP 50 kg + Sulphur (bentonite) 8 kg per acre at sowing.", "FYM 4 tonne per acre + Azotobacter."},
                {"Urea 30 kg per acre at first irrigation.", "Vermicompost 400 kg per acre."},
                {"Urea 10 kg per acre only if crop is pale.", "Panchagavya 3% foliar spray."}});
        FERTILIZERS.put("Watermelon", new String[][]{
                {"DAP 50 kg + MOP 25 kg per acre in pits/beds.", "FYM 6 tonne per acre + neem cake 100 kg."},
                {"Urea 25 kg per acre at vine growth.", "Vermicompost 500 kg + Jeevamrut."},
                {"MOP 25 kg per acre at fruit set (improves sweetness).", "Wood ash 50 kg + compost tea."}});

        // ---------- PESTS: {name, symptoms, chemical control, organic control} ----------
        PESTS.put("Rice", new String[][]{
                {"Stem Borer", "Dead hearts in young plants, white empty panicles.", "Cartap hydrochloride or chlorantraniliprole as per label.", "Pheromone traps, Trichogramma egg cards, neem seed extract 5%."},
                {"Blast", "Diamond-shaped grey spots on leaves and neck.", "Tricyclazole as per label.", "Pseudomonas fluorescens spray; avoid excess nitrogen."}});
        PESTS.put("Wheat", new String[][]{
                {"Aphids", "Clusters of small insects on ears and leaves; sticky honeydew.", "Imidacloprid as per label.", "Neem oil 3%, ladybird beetle conservation."},
                {"Yellow Rust", "Yellow powdery stripes on leaves.", "Propiconazole as per label.", "Resistant varieties; early sowing."}});
        PESTS.put("Maize", new String[][]{
                {"Fall Armyworm", "Ragged holes in leaves, frass in whorl.", "Emamectin benzoate or spinetoram as per label.", "Neem seed extract 5%, sand+lime in whorl, pheromone traps."},
                {"Stem Borer", "Dead heart, small holes on stem.", "Carbofuran granules in whorl as per label.", "Trichogramma cards, Bt spray."}});
        PESTS.put("Groundnut", new String[][]{
                {"Leaf Miner", "Brown blotches/mines on leaflets.", "Dimethoate as per label.", "Neem oil 3%, light traps."},
                {"Tikka Leaf Spot", "Dark circular spots with yellow halo.", "Mancozeb or chlorothalonil as per label.", "Crop rotation, remove infected residue, Trichoderma."}});
        PESTS.put("Cotton", new String[][]{
                {"Pink Bollworm", "Rosette flowers, damaged bolls with larvae.", "Profenofos as per label; follow IPM advice.", "Pheromone traps, destroy crop residue, Bt cotton refuge."},
                {"Whitefly", "Yellowing leaves, sooty mould, leaf curl.", "Diafenthiuron as per label.", "Yellow sticky traps, neem oil 3%."}});
        PESTS.put("Soybean", new String[][]{
                {"Girdle Beetle", "Wilting of branches with two ring cuts.", "Triazophos as per label.", "Early sowing, neem seed extract 5%."},
                {"Yellow Mosaic Virus", "Yellow patches on leaves; spread by whitefly.", "Control whitefly with thiamethoxam as per label.", "Resistant varieties, yellow sticky traps, rogue infected plants."}});
        PESTS.put("Sugarcane", new String[][]{
                {"Early Shoot Borer", "Dead hearts in young shoots.", "Chlorantraniliprole granules as per label.", "Trash mulching, Trichogramma cards."},
                {"Red Rot", "Red patches with white spots inside the cane.", "Carbendazim setts treatment as per label.", "Disease-free setts, hot-water treatment, rotation."}});
        PESTS.put("Bajra (Pearl Millet)", new String[][]{
                {"Downy Mildew", "Pale streaks on leaves, green ear (leafy panicles).", "Metalaxyl seed treatment as per label.", "Resistant hybrids, rogue infected plants."},
                {"Shoot Fly", "Dead heart in seedlings.", "Seed treatment with thiamethoxam as per label.", "Early sowing, higher seed rate then thinning."}});
        PESTS.put("Chickpea (Gram)", new String[][]{
                {"Pod Borer (Helicoverpa)", "Holes in pods, larvae feeding on grains.", "Emamectin benzoate as per label.", "HaNPV spray, pheromone traps, bird perches."},
                {"Fusarium Wilt", "Sudden drying of whole plants.", "Carbendazim + thiram seed treatment as per label.", "Trichoderma seed treatment, resistant varieties, rotation."}});
        PESTS.put("Mustard", new String[][]{
                {"Mustard Aphid", "Colonies on stems and pods; curled inflorescence.", "Dimethoate/imidacloprid as per label.", "Early sowing, neem oil 3%, yellow sticky traps."},
                {"White Rust", "White blisters on leaf underside.", "Metalaxyl + mancozeb as per label.", "Clean seed, crop rotation."}});
        PESTS.put("Watermelon", new String[][]{
                {"Fruit Fly", "Punctured fruits with maggots inside.", "Malathion bait spray as per label.", "Cue-lure traps, collect and bury fallen fruit."},
                {"Powdery Mildew", "White powdery patches on leaves.", "Wettable sulphur as per label.", "Milk spray (1:9), Trichoderma, wider spacing."}});
    }

    /** {name, activity} per stage; a safe generic default if the crop is unknown. */
    public static String[][] getStageText(String cropName) {
        String[][] s = STAGES.get(cropName);
        if (s != null) return s;
        return new String[][]{
                {"Sowing", "Prepare land and sow."}, {"Vegetative", "Weeding and irrigation."},
                {"Flowering", "Protect from stress."}, {"Fruit/Grain Development", "Monitor pests."},
                {"Harvest", "Harvest at maturity."}};
    }

    /** Fertilizer advice for stage 0-2 (basal, vegetative, flowering); null for stages 3-4. */
    public static FertilizerAdvice getFertilizer(String cropName, int stageIndex) {
        String[][] f = FERTILIZERS.get(cropName);
        if (f == null || stageIndex < 0 || stageIndex >= f.length) return null;
        return new FertilizerAdvice(f[stageIndex][0], f[stageIndex][1]);
    }

    public static List<PestInfo> getPests(String cropName) {
        List<PestInfo> list = new ArrayList<>();
        String[][] p = PESTS.get(cropName);
        if (p != null) {
            for (String[] row : p) list.add(new PestInfo(row[0], row[1], row[2], row[3]));
        }
        return list;
    }
}
