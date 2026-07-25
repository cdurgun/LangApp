package com.langapp.content;

/** VerbConjugation + aspect eşleşme bilgisini JSON'a güvenli sekilde tasimak icin DTO. */
public record ConjugationView(
        String present1s, String present2s, String present3s,
        String present1p, String present2p, String present3p,
        String future1s, String future2s, String future3s,
        String future1p, String future2p, String future3p,
        String imperativeSingular, String imperativePlural,
        String aspect,
        String pairSourceText,
        String pairTargetText,
        Long pairId
) {
    /** Cekim kaydi yoksa (henuz eklenmemisse) tum cekim alanlari icin null geçilebilir. */
    public static ConjugationView build(VerbConjugation vc, VocabItem item) {
        String aspectLabel = item.getAspect() != null ? item.getAspect().getTurkishLabel() : null;
        VocabItem pair = item.getAspectPair();

        return new ConjugationView(
                vc != null ? vc.getPresent1s() : null,
                vc != null ? vc.getPresent2s() : null,
                vc != null ? vc.getPresent3s() : null,
                vc != null ? vc.getPresent1p() : null,
                vc != null ? vc.getPresent2p() : null,
                vc != null ? vc.getPresent3p() : null,
                vc != null ? vc.getFuture1s() : null,
                vc != null ? vc.getFuture2s() : null,
                vc != null ? vc.getFuture3s() : null,
                vc != null ? vc.getFuture1p() : null,
                vc != null ? vc.getFuture2p() : null,
                vc != null ? vc.getFuture3p() : null,
                vc != null ? vc.getImperativeSingular() : null,
                vc != null ? vc.getImperativePlural() : null,
                aspectLabel,
                pair != null ? pair.getSourceText() : null,
                pair != null ? pair.getTargetText() : null,
                pair != null ? pair.getId() : null
        );
    }
}
