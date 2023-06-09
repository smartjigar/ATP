package org.catenax.atp.endpoints.request;


import org.catenax.atp.utils.constant.AppConstants;

import jakarta.validation.constraints.NotBlank;

public record DeleteGroupRequest(@NotBlank(message = AppConstants.GROUP_NAME_REQUIRED) String groupName) {
}
