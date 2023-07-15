package com.example.co_fitness.Callbacks;


import com.example.co_fitness.Model.Offer;

public interface OfferCallback {
    void joinClicked(Offer offer, int position);
    void itemClicked(Offer offer, int position);
    void leaveClicked(Offer offer, int position);
    void deleteClicked(Offer offer, int position);
    void editClicked(Offer offer, int position);
}
