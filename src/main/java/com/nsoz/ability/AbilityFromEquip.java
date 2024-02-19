package com.nsoz.ability;

import com.nsoz.constants.ItemOptionName;
import com.nsoz.item.Equip;
import com.nsoz.item.Item;
import com.nsoz.item.ItemManager;
import com.nsoz.item.Mount;
import com.nsoz.model.Char;
import com.nsoz.option.ItemOption;
import com.nsoz.option.PotentialName;
import com.nsoz.option.SkillOption;
import com.nsoz.option.SkillOptionName;
import com.nsoz.server.GameData;
import com.nsoz.skill.Skill;

public class AbilityFromEquip implements AbilityStrategy {

    @Override
    public void setAbility(Char owner) {
        owner.updateKickOption();
        int length = ItemManager.getInstance().getOptionSize();
        owner.options = new int[length];
        owner.haveOptions = new boolean[length];
        length = GameData.getInstance().getOptionTemplates().size();
        owner.optionsSupportSkill = new int[length];
        for (Equip equip : owner.equipment) {
            if (equip != null) {
                if (!equip.isExpired()) {
                    for (ItemOption o : equip.getOptions()) {
                        int optionID = o.optionTemplate.id;
                        if (o.optionTemplate.type >= 2 && o.optionTemplate.type <= 7) {
                            if (o.active == 1) {
                                owner.options[optionID] += o.param;
                                owner.haveOptions[optionID] = true;
                            }
                        } else {
                            owner.options[optionID] += o.param;
                            owner.haveOptions[optionID] = true;
                        }
                    }
                }
            }
        }
        for (Equip equip : owner.fashion) {
            if (equip != null) {
                if (!equip.isExpired()) {
                    for (ItemOption o : equip.getOptions()) {
                        int optionID = o.optionTemplate.id;
                        if (o.optionTemplate.type >= 3 && o.optionTemplate.type <= 7) {
                            if ((o.optionTemplate.type == 3 && equip.upgrade >= 4) || (o.optionTemplate.type == 4 && equip.upgrade >= 8)
                                    || (o.optionTemplate.type == 5 && equip.upgrade >= 12) || (o.optionTemplate.type == 6 && equip.upgrade >= 14)
                                    || (o.optionTemplate.type == 7 && equip.upgrade >= 16)) {
                                owner.options[optionID] += o.param;
                                owner.haveOptions[optionID] = true;
                            }
                        } else {
                            owner.options[optionID] += o.param;
                            owner.haveOptions[optionID] = true;
                        }
                    }
                }

            }
        }
        if (owner.mount[4] != null && (!owner.mount[4].isExpired())) {
            for (Mount mount : owner.mount) {
                if (mount != null) {
                    for (ItemOption o : mount.getOptions()) {
                        int optionID = o.optionTemplate.id;
                        owner.options[optionID] += o.param;
                        owner.haveOptions[optionID] = true;
                    }
                }
            }
        }
        Item mask = owner.getMask();
        if (mask != null) {
            for (ItemOption o : mask.getOptions()) {
                int optionID = o.optionTemplate.id;
                owner.options[optionID] += o.param;
                owner.haveOptions[optionID] = true;
            }
        }

        Skill selectedSkill = owner.selectedSkill;
        int selectedSkillOptions[] = new int[length];
        if (selectedSkill != null) {
            for (SkillOption option : selectedSkill.options) {
                int optionId = option.optionTemplate.id;
                int param = option.param;
                selectedSkillOptions[optionId] += param;
            }
        }

        for (Skill skill : owner.vSupportSkill) {
            for (SkillOption option : skill.options) {
                owner.optionsSupportSkill[option.optionTemplate.id] += option.param;
            }
        }

        int potentialDmg = owner.getSideClass() == 0 ? owner.potential[PotentialName.SUC_MANH] : owner.potential[PotentialName.CHAKRA];
        potentialDmg += owner.options[ItemOptionName.ADD_POINT_DIEM_TIEM_NANG_CHO_TAT_CA_TYPE_1]
                + (potentialDmg * owner.options[ItemOptionName.CONG_THEM_TIEM_NANG_ADD_POINT_PERCENT_TYPE_0] / 100);
        owner.damage = potentialDmg;
        owner.damage += owner.options[ItemOptionName.UPGRADE_12_TAN_CONG_CO_BAN_CUA_VU_KHI_ADD_POINT_TYPE_5]
                + (potentialDmg * selectedSkillOptions[SkillOptionName.TAN_CONG_CO_BAN_ADD_POINT_PERCENT] / 100);

        if (owner.incrDame > 0) {
            owner.damage += potentialDmg * owner.incrDame / 100;
        }
        if (owner.incrDame2 > 0) {
            owner.damage += potentialDmg * owner.incrDame2 / 100;
        }
        if (owner.incrDame3 > 0) {
            owner.damage += potentialDmg * owner.incrDame3 / 100;
        }

        switch (owner.getSideClass()) {
            case 0: // Ngoại công
                owner.damage += (potentialDmg * selectedSkillOptions[SkillOptionName.TAN_CONG_NGOAI_ADD_POINT_PERCENT] / 100)
                        + owner.options[ItemOptionName.TAN_CONG_NGOAI_ADD_POINT_TYPE_0]
                        + (potentialDmg * owner.options[ItemOptionName.VAT_CONG_NGOAI_ADD_POINT_PERCENT_TYPE_1] / 100);
                break;

            case 1: // Nội công
                owner.damage += (potentialDmg * selectedSkillOptions[SkillOptionName.TAN_CONG_NOI_ADD_POINT_PERCENT] / 100)
                        + owner.options[ItemOptionName.TAN_CONG_NOI_ADD_POINT_TYPE_0]
                        + (potentialDmg * owner.options[ItemOptionName.VAT_CONG_NOI_ADD_POINT_PERCENT_TYPE_1] / 100);
                break;
        }

        int supportDmg = 0;
        switch (owner.getSys()) {
            case 1: // Hoả hệ
                owner.damage += selectedSkillOptions[SkillOptionName.HOA_CONG_ADD_POINT] + owner.options[ItemOptionName.HOA_CONG_ADD_POINT_TYPE_8];
                supportDmg = selectedSkillOptions[SkillOptionName.HOA_CONG_ADD_POINT];
                break;
            case 2: // Băng hệ
                owner.damage += selectedSkillOptions[SkillOptionName.BANG_SAT_ADD_POINT] + owner.options[ItemOptionName.BANG_CONG_ADD_POINT_TYPE_8];
                supportDmg = selectedSkillOptions[SkillOptionName.BANG_SAT_ADD_POINT];
                break;
            case 3: // Phong hệ
                owner.damage += selectedSkillOptions[SkillOptionName.PHONG_LOI_ADD_POINT] + owner.options[ItemOptionName.PHONG_LOI_ADD_POINT_TYPE_8];
                supportDmg = selectedSkillOptions[SkillOptionName.PHONG_LOI_ADD_POINT];
                break;
        }
        switch (owner.classId) {
            case 1: // Kiếm
                owner.damage += selectedSkillOptions[SkillOptionName.HOA_CONG_NGOAI_ADD_POINT]
                        + owner.options[ItemOptionName.HOA_CONG_NGOAI_ADD_POINT_TYPE_2];
                break;
            case 2: // Tiêu
                owner.damage +=
                        selectedSkillOptions[SkillOptionName.HOA_CONG_NOI_ADD_POINT] + owner.options[ItemOptionName.HOA_CONG_NOI_ADD_POINT_TYPE_2];
                break;
            case 3: // Kunai
                owner.damage += selectedSkillOptions[SkillOptionName.BANG_SAT_NGOAI_ADD_POINT]
                        + owner.options[ItemOptionName.BANG_SAT_NGOAI_ADD_POINT_TYPE_2];
                break;
            case 4: // Cung
                owner.damage +=
                        selectedSkillOptions[SkillOptionName.BANG_SAT_NOI_ADD_POINT] + owner.options[ItemOptionName.BANG_SAT_NOI_ADD_POINT_TYPE_2];
                break;
            case 5: // Đao
                owner.damage += selectedSkillOptions[SkillOptionName.PHONG_LOI_NGOAI_ADD_POINT]
                        + owner.options[ItemOptionName.PHONG_LOI_NGOAI_ADD_POINT_TYPE_2];
                break;
            case 6: // Quạt
                owner.damage +=
                        selectedSkillOptions[SkillOptionName.PHONG_LOI_NOI_ADD_POINT] + owner.options[ItemOptionName.PHONG_LOI_NOI_ADD_POINT_TYPE_2];
                break;
        }
        owner.damage += owner.options[ItemOptionName.TAN_CONG_POINT_TYPE_1] + owner.options[ItemOptionName.TANG_TAN_CONG_CHO_CHU_ADD_POINT_TYPE_8]
                + owner.options[ItemOptionName.TAN_CONG_ADD_POINT_TYPE_8]
                + (potentialDmg * owner.options[ItemOptionName.TAN_CONG_ADD_POINT_PERCENT_TYPE_8] / 100)
                + owner.options[ItemOptionName.TAN_CONG_ADD_POINT_TYPE_1];

        boolean isSkill30 = false;
        boolean isSkill40 = false;
        boolean isSkill50 = false;
        if (selectedSkill != null) {
            int level = selectedSkill.template.skills.get(0).level;
            isSkill30 = level == 30;
            isSkill40 = level == 40;
            isSkill50 = level == 50;
        }
        int[] skillOptions2 = new int[length];
        for (Skill skill : owner.vSkillFight) {
            if (skill.template.type == Skill.SKILL_CLICK_USE_ATTACK) {
                for (SkillOption option : skill.options) {
                    skillOptions2[option.optionTemplate.id] += option.param;
                }
            }
        }

        if (isSkill30) {
            owner.damage += supportDmg * skillOptions2[SkillOptionName.HO_TRO_TAN_CONG_CHIEU_CAP_30_ADD_POINT_PERCENT] / 100;
        }
        if (isSkill40) {
            owner.damage += supportDmg * skillOptions2[SkillOptionName.HO_TRO_TAN_CONG_CHIEU_CAP_40_ADD_POINT_PERCENT] / 100;
        }
        if (isSkill50) {
            owner.damage += supportDmg * skillOptions2[SkillOptionName.HO_TRO_TAN_CONG_CHIEU_CAP_50_ADD_POINT_PERCENT] / 100;
        }
        owner.damage2 = owner.damage - owner.damage / 10;


        int potentialTheLuc = owner.potential[PotentialName.THE_LUC] + owner.options[ItemOptionName.ADD_POINT_DIEM_TIEM_NANG_CHO_TAT_CA_TYPE_1]
                + (owner.potential[PotentialName.THE_LUC] * owner.options[ItemOptionName.CONG_THEM_TIEM_NANG_ADD_POINT_PERCENT_TYPE_0] / 100);
        int basicHP = potentialTheLuc * 10;
        owner.maxHP = basicHP;
        owner.maxHP += basicHP * (owner.options[ItemOptionName.UPGRADE_8_TI_LE_HP_TOI_DA_ADD_POINT_PERCENT_TYPE_4]
                + owner.options[ItemOptionName.UPGRADE_8_TI_LE_HP_TOI_DA_ADD_POINT_PERCENT_TYPE_4_2]
                + owner.optionsSupportSkill[SkillOptionName.TI_LE_HP_TOI_DA_ADD_POINT_PERCENT]) / 100;
        owner.maxHP += owner.options[ItemOptionName.HP_TOI_DA_ADD_POINT_TYPE_1] + owner.options[ItemOptionName.UPGRADE_12_HP_TOI_DA_ADD_POINT_TYPE_5]
                + owner.options[ItemOptionName.TANG_MAX_HP_CHO_CHU_ADD_POINT_TYPE_8] + owner.options[ItemOptionName.HP_TOI_DA_POINT_TYPE_8]
                + owner.options[ItemOptionName.HP_TOI_DA_POINT_TYPE_1] + owner.options[ItemOptionName.HP_TOI_DA_ADD_POINT_TYPE_1_2];
        owner.maxHP += owner.maxHP
                * (owner.options[ItemOptionName.HP_TOI_DA_POINT_PERCENT_TYPE_0] + owner.options[ItemOptionName.HP_TOI_DA_POINT_PERCENT_TYPE_0_2])
                / 100;
        owner.maxHP += owner.incrHP;
        if (owner.maxHP <= 0) {
            owner.maxHP = 50;
        }
        if (owner.hp > owner.maxHP) {
            owner.hp = owner.maxHP;
        }

        int potentialChakra = owner.potential[PotentialName.CHAKRA] + owner.options[ItemOptionName.ADD_POINT_DIEM_TIEM_NANG_CHO_TAT_CA_TYPE_1]
                + (owner.potential[PotentialName.CHAKRA] * owner.options[ItemOptionName.CONG_THEM_TIEM_NANG_ADD_POINT_PERCENT_TYPE_0] / 100);
        int basicMP = owner.getSideClass() == 0 ? potentialChakra * 10 : (potentialChakra * 10) * 40 / 100;
        owner.maxMP = basicMP;
        owner.maxMP += basicMP * (owner.options[ItemOptionName.UPGRADE_8_TI_LE_MP_TOI_DA_ADD_POINT_PERCENT_TYPE_4]
                + owner.options[ItemOptionName.UPGRADE_4_TI_LE_MP_TOI_DA_ADD_POINT_PERCENT_TYPE_3]
                + owner.optionsSupportSkill[SkillOptionName.TI_LE_MP_TOI_DA_ADD_POINT_PERCENT]) / 100;
        owner.maxMP += owner.options[ItemOptionName.MP_TOI_DA_ADD_POINT_TYPE_1] + owner.options[ItemOptionName.MP_TOI_DA_POINT_TYPE_2]
                + owner.options[ItemOptionName.UPGRADE_12_MP_TOI_DA_ADD_POINT_TYPE_5] + owner.options[ItemOptionName.MP_TOI_DA_ADD_POINT_TYPE_8]
                + owner.options[ItemOptionName.MP_TOI_DA_POINT_TYPE_1] + owner.options[ItemOptionName.MP_TOI_DA_ADD_POINT_TYPE_1_2];
        if (owner.maxMP <= 0) {
            owner.maxMP = 50;
        }
        if (owner.mp > owner.maxMP) {
            owner.mp = owner.maxMP;
        }

        owner.dameDown = owner.options[ItemOptionName.GIAM_TRU_SAT_THUONG_ADD_POINT_TYPE_0]
                + owner.options[ItemOptionName.CHIU_SAT_THUONG_CHO_CHU_ADD_POINT_TYPE_1]
                + owner.options[ItemOptionName.GIAM_TRU_SAT_THUONG_POINT_TYPE_8] + owner.options[ItemOptionName.GIAM_TRU_SAT_THUONG_POINT_TYPE_1];

        int potentialThanPhap = owner.potential[PotentialName.THAN_PHAP] + owner.options[ItemOptionName.ADD_POINT_DIEM_TIEM_NANG_CHO_TAT_CA_TYPE_1]
                + (owner.potential[PotentialName.THAN_PHAP] * owner.options[ItemOptionName.CONG_THEM_TIEM_NANG_ADD_POINT_PERCENT_TYPE_0] / 100);

        owner.miss = potentialThanPhap * 150 / 100;
        owner.miss += owner.options[ItemOptionName.NE_DON_ADD_POINT_TYPE_0] + owner.options[ItemOptionName.NE_DON_POINT_TYPE_2]
                + owner.options[ItemOptionName.UPGRADE_12_NE_DON_ADD_POINT_TYPE_5] + owner.options[ItemOptionName.NE_DON_ADD_POINT_TYPE_1]
                + owner.options[ItemOptionName.TANG_NE_TRANH_CHO_CHU_ADD_POINT_TYPE_8] + owner.options[ItemOptionName.NE_DON_ADD_POINT_TYPE_8]
                + owner.options[ItemOptionName.NE_DON_POINT_TYPE_1] + owner.options[ItemOptionName.NE_DON_ADD_POINT_TYPE_1_2]
                + owner.optionsSupportSkill[SkillOptionName.NE_DON_ADD_POINT];

        owner.exactly = potentialThanPhap;
        owner.exactly += owner.options[ItemOptionName.CHINH_XAC_ADD_POINT_TYPE_1] + owner.options[ItemOptionName.CHINH_XAC_POINT_TYPE_2]
                + owner.options[ItemOptionName.TANG_CHINH_XAC_CHO_CHU_ADD_POINT_TYPE_8] + owner.options[ItemOptionName.CHINH_XAC_ADD_POINT_TYPE_8]
                + owner.options[ItemOptionName.CHINH_XAC_POINT_TYPE_1] + owner.options[ItemOptionName.CHINH_XAC_POINT_TYPE_2_2]
                + owner.optionsSupportSkill[SkillOptionName.CHINH_XAC_ADD_POINT];

        int resAll = owner.options[ItemOptionName.KHANG_TAT_CA_POINT_TYPE_2] + owner.options[ItemOptionName.UPGRADE_14_KHANG_TAT_CA_ADD_POINT_TYPE_6]
                + owner.options[ItemOptionName.KHANG_TAT_CA_ADD_POINT_TYPE_8] + owner.options[ItemOptionName.KHANG_TAT_CA_POINT_TYPE_1]
                + owner.optionsSupportSkill[SkillOptionName.KHANG_TAT_CA_ADD_POINT];

        owner.resFire = owner.options[ItemOptionName.KHANG_HOA_ADD_POINT_TYPE_0] + owner.options[ItemOptionName.KHANG_HOA_ADD_POINT_TYPE_2]
                + owner.options[ItemOptionName.UPGRADE_14_KHANG_HOA_ADD_POINT_TYPE_6] + owner.options[ItemOptionName.KHANG_HOA_ADD_POINT_TYPE_1]
                + owner.options[ItemOptionName.KHANG_HOA_ADD_POINT_TYPE_8] + owner.options[ItemOptionName.KHANG_HOA_ADD_POINT_TYPE_1_2]
                + owner.optionsSupportSkill[SkillOptionName.KHANG_HOA_ADD_POINT] + resAll;

        owner.resIce = owner.options[ItemOptionName.KHANG_BANG_ADD_POINT_TYPE_0] + owner.options[ItemOptionName.KHANG_BANG_ADD_POINT_TYPE_2]
                + owner.options[ItemOptionName.UPGRADE_14_KHANG_BANG_ADD_POINT_TYPE_6] + owner.options[ItemOptionName.KHANG_BANG_ADD_POINT_TYPE_1]
                + owner.options[ItemOptionName.KHANG_BANG_ADD_POINT_TYPE_8] + owner.options[ItemOptionName.KHANG_BANG_ADD_POINT_TYPE_1_2]
                + owner.optionsSupportSkill[SkillOptionName.KHANG_BANG_ADD_POINT] + resAll;

        owner.resWind = owner.options[ItemOptionName.KHANG_PHONG_ADD_POINT_TYPE_0] + owner.options[ItemOptionName.KHANG_PHONG_ADD_POINT_TYPE_2]
                + owner.options[ItemOptionName.UPGRADE_14_KHANG_PHONG_ADD_POINT_TYPE_6] + owner.options[ItemOptionName.KHANG_PHONG_ADD_POINT_TYPE_1]
                + owner.options[ItemOptionName.KHANG_PHONG_ADD_POINT_TYPE_8] + owner.options[ItemOptionName.KHANG_PHONG_ADD_POINT_TYPE_1_2]
                + owner.optionsSupportSkill[SkillOptionName.KHANG_PHONG_ADD_POINT] + resAll;

        owner.fatal = owner.options[ItemOptionName.CHI_MANG_POINT_TYPE_2] + owner.options[ItemOptionName.UPGRADE_8_CHI_MANG_ADD_POINT_TYPE_4]
                + owner.options[ItemOptionName.CHI_MANG_ADD_POINT_TYPE_1] + owner.options[ItemOptionName.CHI_MANG_ADD_POINT_TYPE_8]
                + owner.options[ItemOptionName.CHI_MANG_POINT_TYPE_1] + owner.options[ItemOptionName.CHI_MANG_ADD_POINT_TYPE_1_2]
                + owner.optionsSupportSkill[SkillOptionName.CHI_MANG_ADD_POINT];

        owner.fatalDame = owner.options[ItemOptionName.SAT_THUONG_CHI_MANG_POINT_TYPE_1];
        owner.percentFatalDame = owner.options[ItemOptionName.UPGRADE_14_TAN_CONG_KHI_DANH_CHI_MANG_ADD_POINT_PERCENT_TYPE_6]
                + owner.options[ItemOptionName.TAN_CONG_KHI_DANH_CHI_MANG_POINT_PERCENT_TYPE_1]
                + owner.optionsSupportSkill[SkillOptionName.TANG_TAN_CONG_KHI_DANH_CHI_MANG_ADD_POINT_PERCENT];
        owner.reactDame = owner.options[ItemOptionName.PHAN_DON_CAN_CHIEN_ADD_POINT_TYPE_2] + owner.options[ItemOptionName.PHAN_DON_POINT_TYPE_8]
                + owner.options[ItemOptionName.PHAN_DON_POINT_TYPE_1] + owner.optionsSupportSkill[SkillOptionName.PHAN_DON_CAN_CHIEN_ADD_POINT];
        owner.sysDown = 0;
        owner.sysUp = owner.optionsSupportSkill[SkillOptionName.TUONG_KHAC_ADD_POINT] + selectedSkillOptions[SkillOptionName.TUONG_KHAC_ADD_POINT];
        owner.miss += owner.incrMiss;
        owner.exactly += owner.incrExactly;
        owner.resFire += owner.incrRes1;
        owner.resIce += owner.incrRes1;
        owner.resWind += owner.incrRes1;
        owner.resFire += owner.incrRes2;
        owner.resIce += owner.incrRes2;
        owner.resWind += owner.incrRes2;

        // Hoả khắc phong, phong khắc băng, băng khắc hoả
        switch (owner.getSys()) {
            case 1: // Hoả hệ
                owner.resWind += owner.level;
                owner.resIce -= owner.level;
                break;
            case 2: // Băng hệ
                owner.resFire += owner.level;
                owner.resWind -= owner.level;
                break;
            case 3: // Phong hệ
                owner.resIce += owner.level;
                owner.resFire -= owner.level;
                break;
        }

        // Tốc độ di chuyển
        byte speed = 4;
        speed += getSpeedByLevel(owner.level) + getSpeedByOption(owner.optionsSupportSkill[SkillOptionName.TOC_DO_DI_CHUYEN_ADD_POINT],
                owner.options[ItemOptionName.TOC_DO_DI_CHUYEN_ADD_POINT_TYPE_8],
                owner.options[ItemOptionName.TOC_DO_DI_CHUYEN_ADD_POINT_PERCENT_TYPE_2]);
        owner.speed = speed;
    }

    private static byte getSpeedByOption(final int... options) {
        int sum = 0;
        for (int num : options) {
            sum += num;
        }

        return (byte) Math.round(sum / 100f);
    }

    private static byte getSpeedByLevel(final int level) {
        byte speed = 0;
        if (level >= 100) {
            speed = 4;
        } else if (level >= 70) {
            speed = 3;
        } else if (level >= 40) {
            speed = 2;
        } else if (level >= 10) {
            speed = 1;
        }
        return speed;
    }
}
