// --------------------------------------------------------------------------
// Copyright (c) 1998-2004, Drew Davidson and Luke Blanshard
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are
// met:
//
// Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
// Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
// Neither the name of the Drew Davidson nor the names of its contributors
// may be used to endorse or promote products derived from this software
// without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
// FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
// COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
// INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
// BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
// OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
// AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
// THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
// DAMAGE.
// --------------------------------------------------------------------------
package ognl;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;

/**
 * This class provides methods for setting up and restoring
 * access in a Field. Java 2 provides access utilities for setting
 * and getting fields that are non-public. This object provides
 * coarse-grained access controls to allow access to private, protected
 * and package protected members. This will apply to all classes
 * and members.
 *
 * @author Luke Blanshard (blanshlu@netscape.net)
 * @author Drew Davidson (drew@ognl.org)
 * @version 15 October 1999
 */
public class UnitilsMemberAccess
    implements MemberAccess {
    /*
     * Assign an accessibility modification mechanism, based on Major Java Version.
     * Note: Can be override using a Java option flag {@link OgnlRuntime#USE_PREJDK9_ACESS_HANDLER}.
     */
    private static final AccessibleObjectHandler _accessibleObjectHandler;

    static {
        _accessibleObjectHandler = OgnlRuntime.usingJDK9PlusAccessHandler() ? AccessibleObjectHandlerJDK9Plus.createHandler()
            : AccessibleObjectHandlerPreJDK9.createHandler();
    }

    /*
     * ===================================================================
     * MemberAccess interface
     * ===================================================================
     */
    @Override
    public Object setup(OgnlContext context, Object target, Member member, String propertyName) {
        Object result = null;

        if (isAccessible(context, target, member, propertyName)) {
            AccessibleObject accessible = (AccessibleObject) member;

            if (!accessible.isAccessible()) {
                result = Boolean.FALSE;
                _accessibleObjectHandler.setAccessible(accessible, true);
            }
        }
        return result;
    }

    @Override
    public void restore(OgnlContext context, Object target, Member member, String propertyName, Object state) {
        if (state != null) {
            final AccessibleObject accessible = (AccessibleObject) member;
            final boolean stateboolean = ((Boolean) state).booleanValue(); // Using twice (avoid unboxing)
            if (!stateboolean) {
                _accessibleObjectHandler.setAccessible(accessible, stateboolean);
            } else {
                throw new IllegalArgumentException(
                    "Improper restore state [" + stateboolean + "] for target [" + target + "], member [" + member + "], propertyName [" + propertyName + "]");
            }
        }
    }

    /**
     * Returns true if the given member is accessible or can be made accessible
     * by this object.
     *
     * @param context
     *     the current execution context (not used).
     * @param target
     *     the Object to test accessibility for (not used).
     * @param member
     *     the Member to test accessibility for.
     * @param propertyName
     *     the property to test accessibility for (not used).
     * @return true if the member is accessible in the context, false otherwise.
     */
    @Override
    public boolean isAccessible(OgnlContext context, Object target, Member member, String propertyName) {
        return true;
    }
}
