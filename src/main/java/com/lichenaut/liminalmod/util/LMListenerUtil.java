package com.lichenaut.liminalmod.util;

import com.lichenaut.liminalmod.LiminalMod;

public class LMListenerUtil {

        protected final LiminalMod plugin;

        public LMListenerUtil(LiminalMod plugin) {this.plugin = plugin;}

        public boolean chance(int chance) {return Math.random() * 100 < chance;}
}
