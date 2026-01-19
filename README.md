# ![Top Down Sprite Maker](https://raw.githubusercontent.com/jbunke/tdsm-art/refs/heads/master/_tdsm/logo/banner.gif)

<div style="margin: auto; text-align: center; width: 70%;">
    <p style="text-wrap: pretty;">
        <strong>Top Down Sprite Maker (TDSM)</strong> is a flexible and powerful <strong>pixel art character creation tool</strong>. It is a desktop GUI application with executable binaries distributed for Windows, macOS, and Linux distributions.
    </dpiv>
</div>

<div style="text-align: center; margin-top: 50px; margin-bottom: 20px;">
    <div class="store-page-line">
        <a href="https://flinkerflitzer.itch.io/tdsm" target="_blank">
            <img alt="Buy on itch.io" src="https://raw.githubusercontent.com/jbunke/tdsm-art/refs/heads/master/_tdsm/assets/itch-button.png">
        </a>
    </div>
    <div>
        <a href="https://github.com/jbunke/tdsm-api" target="_blank">
            <img alt="API" src="https://raw.githubusercontent.com/jbunke/tdsm-art/refs/heads/master/_tdsm/assets/api-button.png">
        </a>
        <a href="https://github.com/jbunke/tdsm/blob/master/res/text/changelog.txt" target="_blank">
            <img alt="Changelog" src="https://raw.githubusercontent.com/jbunke/tdsm-art/refs/heads/master/_tdsm/assets/changelog-button.png">
        </a>
        <a href="https://raw.githubusercontent.com/jbunke/tdsm-art/refs/heads/master/_tdsm/assets/roadmap-button.png" target="_blank">
            <img alt="Roadmap" src="https://raw.githubusercontent.com/jbunke/tdsm-art/refs/heads/master/_tdsm/assets/roadmap-button.png">
        </a>
    </div>
</div>

## Sprite styles

TDSM is unique among character customization programs in that it supports multiple **sprite styles**, which are distributed separately from the program as ZIP files.

Each sprite style is its own art style, and defines its own...

* Animations
* Layers
* Customization options
* Frame dimensions

Some sprite styles are paid add-ons, while others can be downloaded for free.

The **full list of sprite styles** made or approved by the developer is available [here](https://itch.io/c/5834066/top-down-sprite-maker-approved-sprite-styles).

## Features

### Customize

*TDSM* gives you complete freedom to customize every facet of your character, yet the sprite assembly rules still ensure that sprites always look good and production-ready.

* **Controlled randomization**: Generate random sprites with the click of a button. Randomization can be constrained by **locking** customization layers you wish to exclude. [**[ screenshot ]**](https://raw.githubusercontent.com/jbunke/tdsm-art/refs/heads/master/_tdsm/assets/randomization.gif)
* **Smart layering rules**: Customization layers update dynamically based on changes to other layers they depend on. For example, when using the [*Pixel Citizen*](https://flinkerflitzer.itch.io/pixel-citizen) sprite style, changing your body type will dynamically update your clothes to fit your character's new body, while preserving the selection. [**[ screenshot ]**](https://raw.githubusercontent.com/jbunke/tdsm-art/refs/heads/master/_tdsm/assets/dynamic-choice.gif)

### Configure

Configure your sprite sheet to your exact needs. Have TDSM adapt to your existing projects rather than having to rework code or sprite sheet slicing configurations.

* **Sizing**: Crop or pad individual sprites to your liking, ranging from 1x1 px to 128x128 px [**[ screenshot ]**](https://raw.githubusercontent.com/jbunke/tdsm-art/refs/heads/master/_tdsm/assets/padding.gif)
* **Sequencing**: Determine which directions and animations to include in the export, and **in which order** [**[ screenshot ]**](https://raw.githubusercontent.com/jbunke/tdsm-art/refs/heads/master/_tdsm/assets/sequencing.gif)
* **Layout**: Determine the axis along which directions and animations are exported in the sprite sheet, as well as how distinct animations follow one another [**[ screenshot ]**](https://raw.githubusercontent.com/jbunke/tdsm-art/refs/heads/master/_tdsm/assets/layout.gif)

### Export options

In addition to the sprite sheet as a PNG image file with a transparent background, TDSM gives you the option to export:

* Sprite sheet metadata as a JSON file
* Sprite sheet with customization layers separated onto distinct layers as a [*Stipple Effect*](https://github.com/stipple-effect/stipple-effect) project

### Save and load sprite data

A character from a previously exported sprite sheet can be reloaded into TDSM by uploading its JSON metadata file, provided that the sprite style that produced the sheet is currently loaded into TDSM. [**[ screenshot ]**](https://raw.githubusercontent.com/jbunke/tdsm-art/refs/heads/master/_tdsm/assets/captures/reupload.gif)

## Contribute

* [Report a bug](https://github.com/jbunke/tdsm/issues/new?template=bug_report.md)
* [Request a feature](https://github.com/jbunke/tdsm/issues/new?template=feature_request.md)

## License

*TDSM* is distributed under an [end-user license agreement](./LICENSE) (EULA).

### You may...

* Use *TDSM* for personal or commercial projects
* Clone the *TDSM* source code and privately modify it to suit your needs
* Distribute or sell *TDSM* sprite styles consisting of original work

### You may not...

* Distribute or sell copies of *TDSM* (whether modified or not)
* Use *TDSM* for NFT or crypto-related projects
* Use *TDSM* to train generative AI models

> **Note:**
> 
> Some sprite styles featured in TDSM are based on established IPs. The use of sprites in such styles in commercial projects (fan games, etc.) may infringe upon the copyright of the associated copyright holder.
