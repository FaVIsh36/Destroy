package com.petrolpark.destroy.fluid.ingredient;

import java.util.Collection;
import java.util.List;

import com.google.gson.JsonObject;
import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipHelper.Palette;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;

public class MoleculeFluidIngredient extends ConcentrationRangeFluidIngredient {

    protected Molecule molecule;

    @Override
    public MixtureFluidIngredient getNew() {
        return new MoleculeFluidIngredient();
    };

    @Override
    public String getMixtureFluidIngredientSubtype() {
        return "mixtureFluidWithMolecule";
    };

    @Override
    public void addNBT(CompoundTag fluidTag) {
        super.addNBT(fluidTag);
        fluidTag.putString("MoleculeRequired", molecule.getFullID());
    };

    @Override
    public List<Component> getDescription(CompoundTag fluidTag) {
        String moleculeID = fluidTag.getString("MoleculeRequired");
        float minConc = fluidTag.getFloat("MinimumConcentration");
        float maxConc = fluidTag.getFloat("MaximumConcentration");

        Molecule molecule = Molecule.getMolecule(moleculeID);
        Component moleculeName = molecule == null ? DestroyLang.translate("tooltip.unknown_molecule").component() : molecule.getName(DestroyAllConfigs.CLIENT.chemistry.iupacNames.get());

        return TooltipHelper.cutStringTextComponent(DestroyLang.translate("tooltip.mixture_ingredient.molecule", moleculeName, df.format(minConc), df.format(maxConc)).string(), Palette.GRAY_AND_WHITE);
    };

    @Override
    public Collection<Molecule> getContainedMolecules(CompoundTag fluidTag) {
        String moleculeID = fluidTag.getString("MoleculeRequired");
        Molecule molecule = Molecule.getMolecule(moleculeID);
        if (molecule == null) return List.of();
        return List.of(molecule);
    };

    @Override
    protected boolean testMixture(Mixture mixture) {
        return mixture.hasUsableMolecule(molecule, minConcentration, maxConcentration, null);
    };

    @Override
    protected void readInternal(FriendlyByteBuf buffer) {
        super.readInternal(buffer);
        molecule = Molecule.getMolecule(buffer.readUtf());
    };

    @Override
    protected void writeInternal(FriendlyByteBuf buffer) {
        super.writeInternal(buffer);
        buffer.writeUtf(molecule.getFullID());
    };

    @Override
    protected void readInternal(JsonObject json) {
        super.readInternal(json);
        molecule = Molecule.getMolecule(GsonHelper.getAsString(json, "molecule"));
    };

    @Override
    protected void writeInternal(JsonObject json) {
        super.writeInternal(json);
        json.addProperty("molecule", molecule.getFullID());
    };

};
