package com.lstrainer.lstrainer_api.ficha.dto;

import java.util.List;

public record ReordenarRequest(List<Long> idsNaOrdem) {}