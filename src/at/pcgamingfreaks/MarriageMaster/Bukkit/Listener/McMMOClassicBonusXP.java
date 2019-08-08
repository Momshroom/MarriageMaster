/*
 *   Copyright (C) 2019 GeorgH93
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.MarriageMaster.Bukkit.Listener;

import at.pcgamingfreaks.ConsoleColor;
import at.pcgamingfreaks.MarriageMaster.Bukkit.MarriageMaster;
import at.pcgamingfreaks.Reflection;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

public class McMMOClassicBonusXP extends McMMOBonusXPBase<Object> implements Listener
{
	private static final Method GET_XP_GAIN_REASON = Reflection.getMethodIncludeParents(McMMOPlayerXpGainEvent.class, "getXpGainReason");
	private static final Method GET_SKILL = Reflection.getMethodIncludeParents(McMMOPlayerXpGainEvent.class, "getSkill");
	@SuppressWarnings("SpellCheckingInspection")
	private static final Method ADD_XP = Reflection.getMethod(McMMOPlayer.class, "addXp", Reflection.getClass("com.gmail.nossr50.datatypes.skills.SkillType"), float.class);

	private final Set<String> blockedSources, blockedSkills;

	public McMMOClassicBonusXP(final @NotNull MarriageMaster marriagemaster)
	{
		super(marriagemaster);
		blockedSkills = plugin.getConfiguration().getMcMMOBonusXpBlockedSkills();
		blockedSources = plugin.getConfiguration().getMcMMOBonusXpBlockedSources();
		plugin.getLogger().info(ConsoleColor.GREEN + "mcMMO classic hooked" + ConsoleColor.RESET);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onGainXp(McMMOPlayerXpGainEvent event)
	{
		try
		{
			Object skill = GET_SKILL.invoke(event);
			if(blockedSources.contains(((Enum) GET_XP_GAIN_REASON.invoke(event)).name()) || blockedSkills.contains(((Enum) skill).name())) return;
			onGainXp(event, skill);
		}
		catch(IllegalAccessException | InvocationTargetException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected void addXp(final @NotNull McMMOPlayer player, float xp, final @NotNull Object skill)
	{
		try
		{
			ADD_XP.invoke(player, skill, xp);
		}
		catch(IllegalAccessException | InvocationTargetException e)
		{
			e.printStackTrace();
		}
	}
}