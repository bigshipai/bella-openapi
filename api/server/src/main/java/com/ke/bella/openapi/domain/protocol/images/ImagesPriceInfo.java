package com.ke.bella.openapi.domain.protocol.images;

import com.ke.bella.openapi.common.dto.ComponentList;
import com.ke.bella.openapi.domain.protocol.IPriceInfo;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class ImagesPriceInfo implements IPriceInfo, Serializable {

    @Serial
	private static final long serialVersionUID = 1L;
    ImagesPriceInfoDetailsList details;
    private double batchDiscount = 1.0;
    private double supplierDiscount = 1.0;

    @Override
    public String getUnit() {
        return "CNY/image";
    }

    @Override
    public Map<String, String> description() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("details", "Price details");
        return map;
    }

    public static class ImagesPriceInfoDetailsList extends ArrayList<ImagesPriceInfoDetails> implements ComponentList<ImagesPriceInfoDetails> {
        private static final long serialVersionUID = 1L;

        @Override
        public String toString() {
            if(size() == 0) {
                return "N/A";
            }
            ImagesPriceInfoDetails details = get(0);
            for (ImagesPriceInfoDetails d : this) {
                if("1024x1024".equals(d.getSize())) {
                    details = d;
                }
            }
            return details.toString();
        }

        @Override
        public Class<ImagesPriceInfoDetails> getComponentType() {
            return ImagesPriceInfoDetails.class;
        }
    }

    @Data
    public static class ImagesPriceInfoDetails implements IPriceInfo, Serializable {
        private static final long serialVersionUID = 1L;
        private String size;
        private BigDecimal ldPricePerImage;
        private BigDecimal mdPricePerImage;
        private BigDecimal hdPricePerImage;
        private BigDecimal textTokenPrice;
        private BigDecimal imageTokenPrice;

        @Override
        public String getUnit() {
            return "CNY/image";
        }

        @Override
        public Map<String, String> description() {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("size", "Image size");
            map.put("ldPricePerImage", "Price per image (low quality)");
            map.put("mdPricePerImage", "Price per image (medium quality)");
            map.put("hdPricePerImage", "Price per image (high quality)");
            map.put("textTokenPrice", "Text token (/1k tokens)");
            map.put("imageTokenPrice", "Image token (/1k tokens)");
            return map;
        }

        @Override
        public String toString() {
            return "Size: " + size + "\n" + "Low: " + ldPricePerImage + "\n" + "Medium: " + mdPricePerImage + "\n" + "High: " + hdPricePerImage;
        }
    }
}
