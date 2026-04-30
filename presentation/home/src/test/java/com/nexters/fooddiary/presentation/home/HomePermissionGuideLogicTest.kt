package com.nexters.fooddiary.presentation.home

import com.nexters.fooddiary.core.common.permission.PermissionUtil.MediaAccessState
import org.junit.Assert.assertEquals
import org.junit.Test

class HomePermissionGuideLogicTest {

    @Test
    fun `denied state requests permission first`() {
        assertEquals(
            PermissionGuideEntryAction.REQUEST_PERMISSION,
            permissionGuideEntryAction(MediaAccessState.DENIED)
        )
    }

    @Test
    fun `partial state goes to settings`() {
        assertEquals(
            PermissionGuideEntryAction.OPEN_SETTINGS,
            permissionGuideEntryAction(MediaAccessState.PARTIAL)
        )
        assertEquals(
            PermissionGuideResultAction.OPEN_SETTINGS,
            permissionGuideResultAction(MediaAccessState.PARTIAL)
        )
    }

    @Test
    fun `full state completes guide flow`() {
        assertEquals(
            PermissionGuideEntryAction.COMPLETE,
            permissionGuideEntryAction(MediaAccessState.FULL)
        )
        assertEquals(
            PermissionGuideResultAction.COMPLETE,
            permissionGuideResultAction(MediaAccessState.FULL)
        )
    }
}
