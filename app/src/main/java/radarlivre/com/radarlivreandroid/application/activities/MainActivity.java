package radarlivre.com.radarlivreandroid.application.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;
import radarlivre.com.radarlivreandroid.R;

public class MainActivity extends MaterialNavigationDrawer {

    //
    @Override
    public void init(Bundle bundle) {

        Drawable mapIcon = MaterialDrawableBuilder.with(this)
                .setIcon(MaterialDrawableBuilder.IconValue.AIRPLANE)
                .setColor(Color.WHITE)
                .setToActionbarSize()
                .build();

        Drawable aboutIcon = MaterialDrawableBuilder.with(this)
                .setIcon(MaterialDrawableBuilder.IconValue.LIBRARY_BOOKS)
                .setColor(Color.WHITE)
                .setToActionbarSize()
                .build();

        MaterialSection mapSection = newSection(getString(R.string.drawer_section_map), mapIcon, new MapFragment());
        addSection(mapSection);

        Intent intent = new Intent(this, AboutProjectActivity.class);
        MaterialSection aboutSection = newSection(getString(R.string.drawer_section_about_project), aboutIcon, intent);
        addSection(aboutSection);

        setDefaultSectionLoaded(0);
        enableToolbarElevation();
        disableLearningPattern();

        setDrawerHeaderImage(R.mipmap.ic_sky);
    }

}
