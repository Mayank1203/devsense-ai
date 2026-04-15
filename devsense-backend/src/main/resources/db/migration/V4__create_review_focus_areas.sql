CREATE TABLE review_focus_areas (
                                    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                    review_id UUID REFERENCES reviews(id) ON DELETE CASCADE,
                                    focus_area VARCHAR(100)
);