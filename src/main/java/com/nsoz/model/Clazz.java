package com.nsoz.model;

import java.util.ArrayList;
import java.util.List;
import com.nsoz.skill.SkillTemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class Clazz {

    private int id;
    private String name;
    @Builder.Default
    private List<SkillTemplate> skillTemplates = new ArrayList<>();

    public void addSkillTemplate(SkillTemplate skillTemplate) {
        this.skillTemplates.add(skillTemplate);
    }
}
