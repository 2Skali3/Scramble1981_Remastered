package scramble.model.map.util.elaborator;

import java.util.ArrayList;
import java.util.List;

import scramble.model.common.api.Pair;
import scramble.model.common.impl.PairImpl;
import scramble.model.common.util.BufferedImageManager;
import scramble.model.map.api.MapColumn;
import scramble.model.map.impl.MapColumnImpl;
import scramble.model.map.impl.MapElement;
import scramble.model.map.util.LandUtils;
import scramble.model.map.util.enums.LandBehaviour;
import scramble.model.map.util.enums.LandPart;
import scramble.model.map.util.enums.StagePart;
import scramble.model.map.util.enums.TerrainType;
import scramble.model.map.util.raw.RawData;
import scramble.model.map.util.raw.SegmentRawData;

import java.util.Random;
import java.util.Arrays;
import java.awt.image.BufferedImage;

/**
 * Class for the generation of a MapStage.
 */
public class StageGenerator {

    private static final LandPart[] FLAT = { LandPart.TOP_FLAT_FLOOR,
            LandPart.GORGE_FLOOR };
    private static final LandPart[] DOWN = { LandPart.CROWN_CLIMB, LandPart.STANDARD_CLIMB,
            LandPart.BOOT_CLIMB, LandPart.ROUND_CLIMB };
    private static final LandPart[] UP = { LandPart.MIRROR_CROWN_CLIMB,
            LandPart.MIRROR_STANDARD_CLIMB, LandPart.MIRROR_BOOT_CLIMB, LandPart.MIRROR_ROUND_CLIMB };
    private static final LandPart SUMMIT = LandPart.TRIANGLE_CLIFF;

    private static final LandPart BRICKWALL = LandPart.LIGHT_BRICK_WALL;

    private final Random rand;
    private final Pair<Integer, Integer> currentYCeilingAndFloor;

    private final int[] thresholdsFlat = { 95, 100 };
    private final int[] thresholdsUpDw = { 50, 60, 90, 100 };

    /**
     * Constructor for MapStageGenerator.
     * 
     * @param heightCeilingAndFloor {@link Pair} element that contains the height of
     *                              the ceiling as the first element of the pair and
     *                              the height of the floor as a second element of
     *                              the pair
     * @see Pair
     */
    public StageGenerator(final Pair<Integer, Integer> heightCeilingAndFloor) {
        this.currentYCeilingAndFloor = new PairImpl<>(heightCeilingAndFloor.getFirstElement(),
                heightCeilingAndFloor.getSecondElement());
        this.rand = new Random();
    }

    /**
     * Method that takes raw data relative to a specific map stage and elaborates
     * them.
     * 
     * @param rawData     the raw data relative to the map stage
     * @param stageLength the length of the stage
     * 
     * @return the elaborated data as a {@link List}
     * 
     * @see List
     * @see RawData
     */
    public List<MapColumn> convertDataToMapStage(final RawData rawData, final int stageLength) {

        final Pair<List<MapElement>, Integer> elaboratedDataCeiling;
        final Pair<List<MapElement>, Integer> elaboratedDataFloor;

        elaboratedDataCeiling = this.elaborateRawData(StagePart.CEILING, rawData.getCeiling(), stageLength,
                rawData.getTerrainType());
        currentYCeilingAndFloor.setFirstElement(elaboratedDataCeiling.getSecondElement());

        elaboratedDataFloor = this.elaborateRawData(StagePart.FLOOR, rawData.getFloor(), stageLength,
                rawData.getTerrainType());
        currentYCeilingAndFloor.setSecondElement(elaboratedDataFloor.getSecondElement());

        if (elaboratedDataCeiling.getFirstElement().size() != elaboratedDataFloor.getFirstElement().size()) {
            return new ArrayList<>();
        }
        final List<MapColumn> elaboratedStage = new ArrayList<>();
        final List<MapElement> elaborateCeiling = elaboratedDataCeiling.getFirstElement();
        final List<MapElement> elaborateFloor = elaboratedDataFloor.getFirstElement();

        for (int i = 0; i < elaboratedDataCeiling.getFirstElement().size(); i++) {
            elaboratedStage.add(new MapColumnImpl(elaborateCeiling.get(i), elaborateFloor.get(i),
                    LandUtils.multiplyPixelPerSprite(i), rawData.getTerrainType()));

        }

        return elaboratedStage;
    }

    private LandPart getSprite(final LandBehaviour behavior) {

        if (behavior == LandBehaviour.BRICK) {
            return BRICKWALL;
        }
        if (behavior == LandBehaviour.SUMMIT) {
            return SUMMIT;
        }

        final List<LandPart> flatSprite;
        final List<LandPart> downSprite;
        final List<LandPart> upSprite;

        flatSprite = new ArrayList<>(Arrays.asList(FLAT));
        downSprite = new ArrayList<>(Arrays.asList(DOWN));
        upSprite = new ArrayList<>(Arrays.asList(UP));

        final int selectedItem;
        if (behavior == LandBehaviour.FLAT) {
            selectedItem = rand.nextInt(thresholdsFlat[thresholdsFlat.length - 1]);

            for (int i = 0; i < thresholdsFlat.length - 1; i++) {
                if (selectedItem < thresholdsFlat[i]) {
                    return flatSprite.get(i);
                }
            }
            return flatSprite.get(thresholdsFlat.length - 1);
        }
        selectedItem = rand.nextInt(this.thresholdsUpDw[this.thresholdsUpDw.length - 1]);
        for (int i = 0; i < this.thresholdsUpDw.length; i++) {
            if (selectedItem < this.thresholdsUpDw[i]) {
                if (behavior == LandBehaviour.UP) {
                    return upSprite.get(i);
                }
                return downSprite.get(i);
            }
        }
        if (behavior == LandBehaviour.UP) {
            return upSprite.get(thresholdsUpDw.length - 1);
        }
        return downSprite.get(thresholdsUpDw.length - 1);

    }

    private int getDesiredY(final StagePart stagePart) {
        if (stagePart == StagePart.CEILING) {
            return this.currentYCeilingAndFloor.getFirstElement();
        }
        return this.currentYCeilingAndFloor.getSecondElement();
    }

    private int standardUpadte(final int y, final LandBehaviour behaviour, final StagePart stagePart) {
        if (behaviour == LandBehaviour.UP || behaviour == LandBehaviour.SUMMIT && stagePart == StagePart.FLOOR) {
            return y - 1;
        } else if (behaviour == LandBehaviour.DW) {
            return y + 1;
        }
        return y;
    }

    private int newY(final int y, final LandBehaviour prevBehaviour, final SegmentRawData rawData,
            final StagePart stagePart) {
        final TerrainType terrainType = rawData.getTerrainType();

        if (terrainType == TerrainType.BRICK_COLUMN && rawData.hasSpecificHeight()) {
            return rawData.getHeight();
        }

        final LandBehaviour triggerBehaviour;
        final int incY;
        if (stagePart == StagePart.CEILING) {
            triggerBehaviour = LandBehaviour.UP;
            incY = -1;
        } else {
            triggerBehaviour = LandBehaviour.DW;
            incY = 1;
        }

        int currentY = y;
        if (rawData.getBehaviour() == triggerBehaviour) {
            currentY -= incY;
        } else if (rawData.getBehaviour() == LandBehaviour.SUMMIT && stagePart == StagePart.CEILING) {
            currentY += 1;
        }

        if (prevBehaviour == triggerBehaviour) {
            currentY += incY;
        } else if (prevBehaviour == LandBehaviour.SUMMIT) {
            currentY += incY;
        }

        return currentY;

    }

    private Pair<List<MapElement>, Integer> elaborateRawData(final StagePart stagePart,
            final List<SegmentRawData> rawData, final int stageLength, final TerrainType terrainType) {
        final List<MapElement> elaboratedData = new ArrayList<>();
        int index = 0;

        int length = rawData.get(index).getLength();
        int currentY;
        if (terrainType == TerrainType.GREENLAND) {
            currentY = this.getDesiredY(stagePart);
        } else {
            currentY = rawData.get(index).getHeight();
        }

        LandBehaviour behaviour = rawData.get(index).getBehaviour();
        LandBehaviour prevBehaviour;

        for (int x = 0; x < stageLength; x++) {

            BufferedImage bi = LandUtils.getSprite(this.getSprite(behaviour));
            if (stagePart == StagePart.CEILING) {
                bi = BufferedImageManager.rotateBufferedImageWithDegree(bi, 180);
            }
            if (behaviour == LandBehaviour.BRICK) {
                bi = BufferedImageManager.changeColorClockwise(bi, 0);
            }

            // to-do: sistemare empty space
            if (behaviour != LandBehaviour.EMPTY) {
                elaboratedData.add(new MapElement(
                        LandUtils.multiplyPixelPerSprite(x), LandUtils.multiplyPixelPerSprite(currentY),
                        LandUtils.PIXEL_PER_LAND_SPRITE_SIDE, LandUtils.PIXEL_PER_LAND_SPRITE_SIDE,
                        bi, terrainType, behaviour));
            }

            if (x == length) {
                index++;
                prevBehaviour = behaviour;
                behaviour = rawData.get(index).getBehaviour();
                length = rawData.get(index).getLength() + x;

                currentY = this.newY(currentY, prevBehaviour, rawData.get(index), stagePart);
            }

            currentY = this.standardUpadte(currentY, behaviour, stagePart);

        }

        return new PairImpl<>(elaboratedData, currentY);
    }

}
