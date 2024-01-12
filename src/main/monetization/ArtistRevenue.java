package main.monetization;

public final class ArtistRevenue {
    private String artist;
    private double merchRevenue;
    private double songRevenue;
    private String mostProfitableSong;

    public String getArtist() {
        return artist;
    }

    public void setArtist(final String artist) {
        this.artist = artist;
    }

    public double getMerchRevenue() {
        return merchRevenue;
    }

    public void setMerchRevenue(final double merchRevenue) {
        this.merchRevenue = merchRevenue;
    }

    public double getSongRevenue() {
        return songRevenue;
    }

    public void setSongRevenue(final double songRevenue) {
        this.songRevenue = songRevenue;
    }

    public String getMostProfitableSong() {
        return mostProfitableSong;
    }

    public void setMostProfitableSong(final String mostProfitableSong) {
        this.mostProfitableSong = mostProfitableSong;
    }

    public ArtistRevenue(final ArtistRevenueBuilder builder) {
        this.artist = builder.artist;
        this.merchRevenue = builder.merchRevenue;
        this.songRevenue = builder.songRevenue;
        this.mostProfitableSong = builder.mostProfitableSong;
    }

    //  Builder for Monetization
    public static final class ArtistRevenueBuilder {

        //  Required Parameter
        private String artist;

        //  Optional Parameters
        private double merchRevenue = 0.0;
        private double songRevenue = 0.0;
        private String mostProfitableSong = "N/A";

        public ArtistRevenueBuilder(final String artist) {
            this.artist = artist;
        }

        /**
         * Set calculated merch revenue
         * @param totalMerchRevenue Merch revenue
         * @return Artist Revenue Builder
         */
        public ArtistRevenueBuilder setMerchRevenue(final double totalMerchRevenue) {
            this.merchRevenue = totalMerchRevenue;
            return this;
        }

        /**
         * Set calculated song revenue
         * @param totalSongRevenue Song revenue
         * @return Artist Revenue Builder
         */
        public ArtistRevenueBuilder setSongRevenue(final double totalSongRevenue) {
            this.songRevenue = totalSongRevenue;
            return this;
        }

        /**
         * Set the most profitable song (due to revenue)
         * @param finalMostProfitableSong Most profitable song
         * @return Artist Revenue Builder
         */
        public ArtistRevenueBuilder setMostProfitableSong(final String finalMostProfitableSong) {
            this.mostProfitableSong = finalMostProfitableSong;
            return this;
        }

        /**
         * ArtistRevenue builder
         * @return ArtistRevenue instance
         */
        public ArtistRevenue build() {
            return new ArtistRevenue(this);
        }
    }
}
