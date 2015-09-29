FROM ubuntu:14.04
MAINTAINER Matthew Davis email: matdavis at ebi.ac.uk
# Initially created by Natalja Kurbatova
# Update the image with the latest packages (recommended)
# and install missing packages

ENV DEBIAN_FRONTEND=noninteractive

# install ubuntu dependencies
RUN apt-get update && apt-get install -y \ 
	build-essential \     
        libcurl4-openssl-dev \
        libxml2-dev \
        gnupg \
        wget \
        perl \
        samtools \
        bowtie  && apt-get clean

# install R
RUN sh -c 'echo "deb http://cran.rstudio.com/bin/linux/ubuntu trusty/" >> /etc/apt/sources.list'
RUN gpg --keyserver keyserver.ubuntu.com --recv-key E084DAB9
RUN gpg -a --export E084DAB9 | sudo apt-key add -
RUN apt-get update \
    && apt-get -y install r-base && apt-get clean

# install R packages 
RUN echo "r <- getOption('repos'); r['CRAN'] <- 'http://cran.us.r-project.org'; options(repos = r);" > ~/.Rprofile
RUN Rscript -e "install.packages('R.utils')"
RUN Rscript -e "install.packages('RColorBrewer')"
RUN Rscript -e "install.packages('futile.logger')"
RUN Rscript -e "install.packages('snow')"
RUN Rscript -e "install.packages('bitops')"
RUN Rscript -e "install.packages('hwriter')"
RUN Rscript -e "install.packages('latticeExtra')"
RUN Rscript -e "install.packages('gplots')"
RUN Rscript -e "install.packages('S4Vectors', repos='http://bioconductor.org/packages/release/bioc')"
RUN Rscript -e "install.packages('IRanges', repos='http://bioconductor.org/packages/release/bioc')"
RUN Rscript -e "install.packages('GenomicRanges', repos='http://bioconductor.org/packages/release/bioc')"
RUN Rscript -e "install.packages('ShortRead', repos='http://bioconductor.org/packages/release/bioc')"

# install Kraken
RUN mkdir /kraken
WORKDIR /kraken
RUN wget ftp://ftp.ebi.ac.uk/pub/contrib/enrightlab/kraken/SequenceImp/src/seqimp-latest.tgz \
    && tar -xzf seqimp-latest.tgz \
    && wget ftp://ftp.ebi.ac.uk/pub/contrib/enrightlab/kraken/reaper/binaries/reaper-15-065/linux/tally-15-065 \
    && wget ftp://ftp.ebi.ac.uk/pub/contrib/enrightlab/kraken/reaper/binaries/reaper-15-065/linux/swan-15-065 \
    && wget ftp://ftp.ebi.ac.uk/pub/contrib/enrightlab/kraken/reaper/binaries/reaper-15-065/linux/reaper-15-065 \
    && wget ftp://ftp.ebi.ac.uk/pub/contrib/enrightlab/kraken/reaper/binaries/reaper-15-065/linux/minion-15-065 \
    && mv /kraken/reaper-15-065 /usr/bin/reaper \
    && mv /kraken/tally-15-065 /usr/bin/tally \
    && mv /kraken/swan-15-065 /usr/bin/swan \
    && mv /kraken/minion-15-065 /usr/bin/minion \
    && mv /kraken/seqimp*/ /kraken/seqimp

RUN chmod 771 /usr/bin/reaper \
    && chmod 771 /usr/bin/tally \
    && chmod 771 /usr/bin/swan \
    && chmod 771 /usr/bin/minion 

ENV PATH /kraken/seqimp/bin:$PATH

# check Kraken
CMD ["/kraken/seqimp/bin/imp_commandline.pl", "--system-check"]
