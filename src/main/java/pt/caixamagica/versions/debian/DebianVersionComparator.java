/*
 * Copyright 2014 Caixa Mágica Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pt.caixamagica.versions.debian;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jorge Simões
 */
public final class DebianVersionComparator implements Comparator<DebianVersion> {

    @Override
    public int compare(DebianVersion v1, DebianVersion v2) {
        // compare epoch
        int diff = (int) (v1.getEpoch() - v2.getEpoch());
        if (diff != 0)
            return diff;

        // compare upstream version
        diff = compare(v1.getUpstreamVersion().getVersion(), v2.getUpstreamVersion().getVersion());
        if (diff != 0)
            return diff;

        diff = v1.getUpstreamVersion().getQualifier() != null && v2.getUpstreamVersion().getQualifier() != null
                ? compare(v1.getUpstreamVersion().getQualifier(), v2.getUpstreamVersion().getQualifier())
                : v1.getUpstreamVersion().getQualifier() != null ? -1
                : v2.getUpstreamVersion().getQualifier() != null ? 1 : 0;
        if (diff != 0)
            return diff;

        diff = v1.getUpstreamVersion().getUpgrade() != null && v2.getUpstreamVersion().getUpgrade() != null
                ? compare(v1.getUpstreamVersion().getUpgrade(), v2.getUpstreamVersion().getUpgrade())
                : v1.getUpstreamVersion().getUpgrade() != null ? 1
                : v2.getUpstreamVersion().getUpgrade() != null ? -1 : 0;
        if (diff != 0)
            return diff;

        // compare revision
        if (v1.getDebianRevision() == null || v2.getDebianRevision() == null) {
            diff = v1.getDebianRevision() != null ? 1 : v2.getDebianRevision() != null ? -1 : 0;
            return diff;
        }

        diff = compare(v1.getDebianRevision().getRevision(), v2.getDebianRevision().getRevision());
        if (diff != 0)
            return diff;

        diff = v1.getDebianRevision().getRelease() != null && v2.getDebianRevision().getRelease() != null
                ? v1.getDebianRevision().getRelease().compareTo(v2.getDebianRevision().getRelease())
                : v1.getDebianRevision().getRelease() != null ? -1
                : v2.getDebianRevision().getRelease() != null ? 1 : 0;

        return diff;
    }

    static int compare(String v1, String v2) {
        Pattern pattern = Pattern.compile("(\\d+|\\p{L}+|[^\\p{LD}]+)");
        Matcher matcher1 = pattern.matcher(v1),
                matcher2 = pattern.matcher(v2);
        boolean matched1 = matcher1.find(),
                matched2 = matcher2.find();
        while (matched1 && matched2) {
            int diff = compareNumber(matcher1.group(), matcher2.group());
            if (diff != 0)
                return diff;
            matched1 = matcher1.find();
            matched2 = matcher2.find();
        }
        return matched1 ? 1 : matched2 ? -1 : 0;
    }

    private static int compareNumber(String v1, String v2) {
        if (isNumeric(v1) && isNumeric(v2)) return Long.signum(Long.parseLong(v1) - Long.parseLong(v2));
        return isNumeric(v1) ? -1 : isNumeric(v2) ? 1
                : !isAlpha(v1) && isAlpha(v2) ? 1 : isAlpha(v1) && !isAlpha(v2) ? -1
                : v1.compareTo(v2);
    }

    private static boolean isAlpha(String string) { return string.matches("\\p{Alpha}*"); }
    private static boolean isNumeric(String string) { return string.matches("\\d+"); }

}
