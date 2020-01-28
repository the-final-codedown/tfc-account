package fr.polytech.al.tfc.account.model;

import lombok.*;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
public class Cap {

    @NonNull
    private Integer money;

    @NonNull
    private Integer amountSlidingWindow;
}
