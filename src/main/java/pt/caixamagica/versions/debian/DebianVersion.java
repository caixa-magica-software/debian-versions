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

import pt.caixamagica.versions.VersionException;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jorge Simões
 */
public final class DebianVersion implements Comparable<DebianVersion>, Serializable {

    private static final long serialVersionUID = 1L;
    private static final Pattern PATTERN_REVISION = Pattern.compile("(?:(\\d+):)?(.+)(?:\\-([^\\-]+))");
    private static final Pattern PATTERN_COMPLETE = Pattern.compile("(?:(\\d+):)?(.+)");
    private static final Pattern PATTERN = Pattern.compile("(?:(\\d+):)?((?:(?:\\-\\p{L}+)?[\\p{LD}\\.\\+~]+)+)(?:\\-(.+))?");

    private final long epoch;
    private final DebianUpstreamVersion upstreamVersion;
    private final DebianRevision debianRevision;

    private DebianVersion(long epoch, String upstreamVersion, String debianRevision) {
        this.epoch = epoch;
        if (upstreamVersion == null) throw new VersionException();
        this.upstreamVersion = new DebianUpstreamVersion(upstreamVersion);
        this.debianRevision = debianRevision != null ? new DebianRevision(debianRevision) : null;
    }

    public DebianVersion(String canonicalVersion) {
//        Matcher matcher = PATTERN.matcher(canonicalVersion);
        Matcher matcher = canonicalVersion.matches(PATTERN_REVISION.pattern())
                ? PATTERN_REVISION.matcher(canonicalVersion) : PATTERN_COMPLETE.matcher(canonicalVersion);
        try {
            matcher.find();
            epoch = matcher.group(1) != null ? Long.parseLong(matcher.group(1)) : -1;
            upstreamVersion = new DebianUpstreamVersion(matcher.group(2));
            debianRevision = matcher.groupCount() > 2 && matcher.group(3) != null ? new DebianRevision(matcher.group(3)) : null;
        } catch (IllegalStateException e) {
            throw new VersionException("Version not supported: " + canonicalVersion);
        }
    }

    public static DebianVersion createEmptyVersion() { return new DebianVersion(-1, "0", null); }

    @Override
    public int compareTo(DebianVersion o) {
        int diff = (int) (epoch - o.epoch);
        if (diff != 0)
            return diff;

        diff = upstreamVersion.compareTo(o.upstreamVersion);
        if (diff != 0)
            return diff;

        return debianRevision != null && o.debianRevision != null ? debianRevision.compareTo(o.debianRevision)
                : debianRevision != null ? 1 : o.debianRevision != null ? -1 : 0;
    }

    private boolean hasEpoch() {
        return epoch >= 0;
    }

    long getEpoch() {
        return Math.max(epoch, 0);
    }

    DebianUpstreamVersion getUpstreamVersion() {
        return upstreamVersion;
    }

    DebianRevision getDebianRevision() {
        return debianRevision;
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof DebianVersion && toString().equals(o.toString());
    }

    @Override
    public String toString() {
        return (hasEpoch() ? epoch + ":" : "") + (upstreamVersion != null ? upstreamVersion : 0)
                + (debianRevision != null ? "-" + debianRevision : "");
    }

}
