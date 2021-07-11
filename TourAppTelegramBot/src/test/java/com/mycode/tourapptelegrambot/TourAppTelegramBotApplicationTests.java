package com.mycode.tourapptelegrambot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.AssertionErrors;
import org.springframework.test.web.servlet.MockMvc;

class TourAppTelegramBotApplicationTests {

    @Autowired
    private MockMvc mvc;


//    @BeforeEach
//    void init() {
//
//    }

    void dd(){

    }

//    @Test
//    void stringDataToCreationDTO() throws JsonProcessingException {
//
//        String a = "{\n" +
//                "    \"makeId\": 9,\n" +
//                "    \"modelId\": 4299,\n" +
//                "    \"year\": 2019,\n" +
//                "    \"price\": 23,\n" +
//                "    \"mileage\": 1200,\n" +
//                "    \"fuelType\": \"Gas\",\n" +
//                "    \"bodyType\": \"Hatchback\",\n" +
//                "    \"color\": \"Green\",\n" +
//                "    \"cityId\": 5,\n" +
//                "    \"gearBox\": \"Tiptronic\",\n" +
//                "    \"loanOption\": true,\n" +
//                "    \"barterOption\": true,\n" +
//                "    \"leaseOption\": false,\n" +
//                "    \"cashOption\": true,\n" +
//                "\"description\":\"Car\",\n" +
//                "    \"carSpecIds\": [1,2,4]\n" +
//                "}";
//
//        ListingCreationDTO listing = new ListingCreationDTO();
//        listing.setMakeId(9l);
//        listing.setModelId(4299l);
//        listing.setYear(2019);
//        listing.setPrice(23);
//        listing.setMileage(1200);
//        listing.setFuelType("Gas");
//        listing.setBodyType("Hatchback");
//        listing.setColor("Green");
//        listing.setCityId(5l);
//        listing.setGearBox("Tiptronic");
//        listing.setLoanOption(true);
//        listing.setBarterOption(true);
//        listing.setLeaseOption(false);
//        listing.setCashOption(true);
//        listing.setDescription("Car");
//        listing.setCarSpecIds(new Long[]{1l, 2l, 4l});
//
//        AssertionErrors.assertEquals("", listing, mapper.readValue(a, ListingCreationDTO.class));
//
//
//    }
}
